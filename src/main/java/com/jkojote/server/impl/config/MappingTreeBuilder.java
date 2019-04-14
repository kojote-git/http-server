package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.FunctionalResponse;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.DeleteMapping;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.PatchMapping;
import com.jkojote.server.annotation.PostMapping;
import com.jkojote.server.annotation.PutMapping;
import com.jkojote.server.annotation.RequestMapping;
import com.jkojote.server.exceptions.InvalidControllerBasePathException;
import com.jkojote.server.exceptions.InvalidControllerMethodException;
import com.jkojote.server.exceptions.InvalidMappingException;
import com.jkojote.server.exceptions.InvalidPathTemplateException;
import com.jkojote.server.utils.Preconditions;
import com.jkojote.server.utils.Regex;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MappingTreeBuilder {
	private static final ControllerMethodBuilder METHOD_CREATOR = new ControllerMethodBuilder();
	private HashMap<Class<? extends Annotation>, HttpMethod> annotationMethodMap;
	private Set<Class<? extends Annotation>> methodAnnotations;

	MappingTreeBuilder() {
		annotationMethodMap = new HashMap<>();
		annotationMethodMap.put(GetMapping.class, HttpMethod.GET);
		annotationMethodMap.put(PostMapping.class, HttpMethod.POST);
		annotationMethodMap.put(PatchMapping.class, HttpMethod.PATCH);
		annotationMethodMap.put(PutMapping.class, HttpMethod.PUT);
		annotationMethodMap.put(DeleteMapping.class, HttpMethod.DELETE);
		methodAnnotations = new HashSet<>(annotationMethodMap.keySet());
		methodAnnotations.add(RequestMapping.class);
	}

	MappingTree build(Object controller) {
		Preconditions.checkNotNull(controller);
		String basePath = getBasePath(controller);
		List<Method> annotatedMethods = scanForAnnotatedMethods(controller);
		return buildTree(controller, basePath, annotatedMethods);
	}

	private String getBasePath(Object controller) {
		RequestMapping mapping = controller.getClass().getAnnotation(RequestMapping.class);
		if (mapping == null) {
			return "/";
		}
		String basePath = mapping.value().trim();
		if (!basePath.startsWith("/")) {
			throw new InvalidControllerBasePathException(
				"invalid controller's base path: " + basePath,
				basePath
			);
		}
		return basePath.endsWith("/") ? basePath : basePath + "/";
	}

	private List<Method> scanForAnnotatedMethods(Object controller) {
		Method[] methods = controller.getClass().getMethods();
		List<Method> result = new ArrayList<>();
		for (Method method : methods) {
			if (isAnnotatedMethod(method)) {
				checkMethod(method);
				result.add(method);
			}
		}
		return result;
	}

	private MappingTree buildTree(Object controller, String basePath, List<Method> methods) {
		MappingTree tree = new MappingTree();
		for (Method method : methods) {
			Mapping mapping = getFirstMapping(method);
			checkPathTemplate(mapping.path);
			String resolvedPathTemplate = resolvePathTemplate(basePath, mapping.path);
			ControllerMethod controllerMethod = createControllerMethod(
				resolvedPathTemplate, controller, method
			);
			tree.addControllerMethod(
				resolvedPathTemplate,
				mapping.method,
				controllerMethod,
				MergeConflictOption.THROW_EXCEPTION
			);
		}
		return tree;
	}

	private ControllerMethod createControllerMethod(String pathTemplate, Object object, Method method) {
		try {
			return METHOD_CREATOR.buildOf(pathTemplate, object, method);
		} catch (InvalidMappingException e) {
			throw new InvalidMappingException(
				"cannot map method with template: " + pathTemplate,
				e
			);
		}
	}

	private Mapping getFirstMapping(Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		RequestMapping requestMapping = findAnnotation(RequestMapping.class, annotations);
		if (requestMapping == null) {
			return getFirstMapping(annotations);
		} else {
			return new Mapping(requestMapping.value(), requestMapping.method());
		}
	}

	/*
	 * find first declared mapping annotation (GetMapping, PostMapping etc.)
	 * and return it's mapping
	 */
	private Mapping getFirstMapping(Annotation[] annotations) {
		Annotation mapping = findAnyAnnotation(methodAnnotations, annotations);
		// cannot happen because methods
		// are scanned for mapping annotations beforehand
		if (mapping == null) {
			throw new NullPointerException();
		}
		try {
			Class<? extends Annotation> clazz = mapping.annotationType();
			Method value = clazz.getMethod("value");
			String pathTemplate = (String) value.invoke(mapping);
			HttpMethod httpMethod = annotationMethodMap.get(clazz);
			return new Mapping(pathTemplate.trim(), httpMethod);
		} catch (NoSuchMethodException | IllegalAccessException |
				InvocationTargetException cannotHappen) {
			throw new RuntimeException(cannotHappen);
		}
	}

	private void checkPathTemplate(String pathTemplate) {
		if (!Regex.PATH_TEMPLATE.matcher(pathTemplate).matches()) {
			throw new InvalidPathTemplateException(
				"invalid path template: " + pathTemplate,
				pathTemplate
			);
		}
	}

	private String resolvePathTemplate(String basePath, String pathTemplate) {
		if (pathTemplate.isEmpty()) {
			return basePath;
		}
		StringBuilder builder = new StringBuilder(pathTemplate);
		if (builder.charAt(0) == '/') {
			builder.deleteCharAt(0);
		}
		if (builder.length() == 0) {
			return basePath;
		}
		if (builder.charAt(builder.length() - 1) == '/') {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.insert(0, basePath).toString();
	}

	private Annotation findAnyAnnotation(Set<Class<? extends Annotation>> set, Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (set.contains(annotation.annotationType())) {
				return annotation;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T findAnnotation(Class<T> clazz, Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a.annotationType() == clazz) {
				return (T) a;
			}
		}
		return null;
	}

	private boolean isAnnotatedMethod(Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (methodAnnotations.contains(annotation.annotationType())){
				return true;
			}
		}
		return false;
	}

	private void checkMethod(Method method) {
		checkNotStatic(method);
		checkAccess(method);
		checkReturnType(method);
	}

	private void checkNotStatic(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw new InvalidControllerMethodException(
				"method cannot be static",
				method
			);
		}
	}

	private void checkAccess(Method method) {
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new InvalidControllerMethodException(
				"method must be public",
				method
			);
		}
	}

	private void checkReturnType(Method method) {
		if (method.getReturnType() != HttpResponse.class) {
			throw new InvalidControllerMethodException(
				"return type must be of type HttpResponse",
				method
			);
		}
	}

	private static class Mapping {
		private String path;
		private HttpMethod method;

		public Mapping(String path, HttpMethod method) {
			this.path = path;
			this.method = method;
		}
	}
}