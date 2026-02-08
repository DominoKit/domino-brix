/*
 * Copyright © 2019 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.brix.processor;

import static java.util.Objects.nonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.dominokit.domino.apt.commons.ProcessorUtil;

public class SourceUtil {

  private final HasProcessorEnv env;

  public SourceUtil(HasProcessorEnv env) {
    this.env = env;
  }

  /**
   * gets an annotation on a specified {@link Element} and return the value from the specified
   * parameter, where the parameter value is a {@link Class}
   *
   * @param element classElement the {@link Element} to be checked
   * @param annotation {@link Class} the represent the annotation
   * @param paramName The annotation member parameter that holds the value
   * @return the {@link Optional} of {@link TypeMirror} that represent the value.
   */
  public Optional<TypeMirror> getClassValueFromAnnotation(
      Element element, Class<? extends Annotation> annotation, String paramName) {
    for (AnnotationMirror am : element.getAnnotationMirrors()) {
      if (env.types()
          .isSameType(
              am.getAnnotationType(),
              env.elements().getTypeElement(annotation.getCanonicalName()).asType())) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
            am.getElementValues().entrySet()) {
          if (paramName.equals(entry.getKey().getSimpleName().toString())) {
            AnnotationValue annotationValue = entry.getValue();
            return Optional.of((DeclaredType) annotationValue.getValue());
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * isAssignableFrom. checks if a specific {@link TypeMirror} is assignable from a specific {@link
   * Class}.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @param targetClass a {@link Class} object.
   * @return a boolean.
   */
  public boolean isAssignableFrom(TypeMirror typeMirror, Class<?> targetClass) {
    return env.types()
        .isAssignable(
            env.types()
                .getDeclaredType(env.elements().getTypeElement(targetClass.getCanonicalName())),
            typeMirror);
  }

  /**
   * isAssignableFrom. checks if a specific {@link TypeMirror} is assignable from a specific {@link
   * Class}.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @param targetClass a {@link Class} object.
   * @return a boolean.
   */
  public boolean isAssignableFrom(Class<?> targetClass, TypeMirror typeMirror) {
    return env.types()
        .isAssignable(
            typeMirror,
            env.types()
                .getDeclaredType(env.elements().getTypeElement(targetClass.getCanonicalName())));
  }

  /**
   * Capitalize the first letter of a {@link String}
   *
   * @param input the string
   * @return The new string with capital first letter
   */
  public String capitalizeFirstLetter(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }

  /**
   * Un-capitalize the first letter of a {@link String}
   *
   * @param input the string
   * @return The new string with small first letter
   */
  public String smallFirstLetter(String input) {
    return input.substring(0, 1).toLowerCase() + input.substring(1);
  }

  /**
   * Finds the type mirror of a class defined as an annotation parameter <em>recursively</em>. This
   * method goes through all the super classes of the element.
   *
   * @param element the element
   * @param annotation the annotation
   * @param paramName the class parameter name
   * @return The type mirror of the class, {@link Optional#empty()} otherwise
   * @see ProcessorUtil#getClassValueFromAnnotation(Element, Class, String)
   */
  public Optional<TypeMirror> findClassValueFromClassAnnotation(
      Element element, Class<? extends Annotation> annotation, String paramName) {
    Optional<TypeMirror> result = getClassValueFromAnnotation(element, annotation, paramName);
    if (result.isPresent()) {
      return result;
    }
    TypeMirror superclass = ((TypeElement) element).getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return Optional.empty();
    } else {
      return findClassValueFromClassAnnotation(
          env.types().asElement(superclass), annotation, paramName);
    }
  }

  /**
   * Recursively try to find if the class or its parents implement the specified interface
   *
   * @param element the element
   * @param target the target class
   * @return The type mirror of the class, {@link Optional#empty()} otherwise
   * @see ProcessorUtil#getClassValueFromAnnotation(Element, Class, String)
   */
  public Optional<? extends TypeMirror> findImplementedInterface(Element element, Class<?> target) {
    TypeElement typeElement = (TypeElement) element;
    Optional<? extends TypeMirror> result =
        typeElement.getInterfaces().stream()
            .filter(typeMirror -> extendsInterface(typeMirror, target))
            .findFirst();
    if (result.isPresent()) {
      return result;
    }
    TypeMirror superclass = ((TypeElement) element).getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return Optional.empty();
    } else {
      return findImplementedInterface(env.types().asElement(superclass), target);
    }
  }

  public boolean extendsInterface(TypeMirror typeMirror, Class<?> target) {
    if (isAssignableFrom(target, typeMirror)) {
      return true;
    }

    TypeElement element = (TypeElement) env.types().asElement(typeMirror);
    return element.getInterfaces().stream().anyMatch(t -> extendsInterface(t, target));
  }

  /**
   * Recursively try to find if the class or its parents implement the specified interface
   *
   * @param element the element
   * @param target the target class
   * @return The type mirror of the class, {@link Optional#empty()} otherwise
   * @see ProcessorUtil#getClassValueFromAnnotation(Element, Class, String)
   */
  public Optional<? extends TypeMirror> findTypeArgument(Element element, Class<?> target) {

    DeclaredType declaredType = (DeclaredType) element.asType();

    Optional<? extends TypeMirror> result =
        declaredType.getTypeArguments().stream()
            .filter(typeMirror -> isAssignableFrom(target, typeMirror))
            .findFirst();
    if (result.isPresent()) {
      return result;
    }

    try {
      TypeMirror superclass = ((TypeElement) element).getSuperclass();
      if (superclass.getKind().equals(TypeKind.NONE)) {
        return findInInterfaces(element, target);
      } else {
        result = findTypeArgument(superclass, target);
        if (result.isPresent()) {
          return result;
        } else {
          return findInInterfaces(element, target);
        }
      }
    } catch (Exception e) {
      env.messager()
          .printMessage(
              Diagnostic.Kind.WARNING, "element : " + element.getSimpleName().toString(), element);
      return Optional.empty();
    }
  }

  private Optional<? extends TypeMirror> findInInterfaces(Element element, Class<?> target) {
    TypeElement typeElement = (TypeElement) element;
    return typeElement.getInterfaces().stream()
        .map(typeMirror -> findTypeArgument(typeMirror, target))
        .filter(Optional::isPresent)
        .findFirst()
        .orElse(Optional.empty());
  }

  private Optional<? extends TypeMirror> findInInterfaces(TypeMirror typeMirror, Class<?> target) {
    TypeElement element = (TypeElement) env.types().asElement(typeMirror);
    return element.getInterfaces().stream()
        .map(entry -> findTypeArgument(entry, target))
        .filter(Optional::isPresent)
        .findFirst()
        .orElse(Optional.empty());
  }

  /**
   * Recursively try to find if the class or its parents implement the specified interface
   *
   * @param type the TypeMirror
   * @param target the target class
   * @return The type mirror of the class, {@link Optional#empty()} otherwise
   * @see ProcessorUtil#getClassValueFromAnnotation(Element, Class, String)
   */
  public Optional<? extends TypeMirror> findTypeArgument(TypeMirror type, Class<?> target) {

    DeclaredType declaredType = (DeclaredType) type;

    Optional<? extends TypeMirror> result =
        declaredType.getTypeArguments().stream()
            .filter(typeMirror -> isAssignableFrom(target, typeMirror))
            .findFirst();
    if (result.isPresent()) {
      return result;
    }
    TypeMirror superclass = ((TypeElement) env.types().asElement(type)).getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return findInInterfaces(type, target);
    } else {
      result = findTypeArgument(superclass, target);
      if (result.isPresent()) {
        return result;
      } else {
        return findInInterfaces(type, target);
      }
    }
  }

  /**
   * Searches for an annotation of a specific class in an element.
   *
   * @param element the element
   * @param annotation the annotation class
   * @param <A> any type extends {@link Annotation}
   * @return The annotation if the element or any of its super classes has it, {@code null}
   *     otherwise
   */
  public <A extends Annotation> A findClassAnnotation(Element element, Class<A> annotation) {
    A result = element.getAnnotation(annotation);
    if (nonNull(result)) {
      return result;
    }
    TypeMirror superclass = ((TypeElement) element).getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return null;
    } else {
      return findClassAnnotation(env.types().asElement(superclass), annotation);
    }
  }

  /**
   * Returns all methods annotated with an annotation
   *
   * @param beanType the type to search in
   * @param annotation the target annotation
   * @return list of methods that are annotated with the annotation
   */
  public List<Element> getAnnotatedMethods(
      TypeMirror beanType, Class<? extends Annotation> annotation) {
    return getAnnotatedElements(
        beanType, annotation, element -> ElementKind.METHOD.equals(element.getKind()));
  }

  /**
   * Returns all fields annotated with an annotation
   *
   * @param beanType the type to search in
   * @param annotation the target annotation
   * @return list of fields that are annotated with the annotation
   */
  public List<Element> getAnnotatedFields(
      TypeMirror beanType, Class<? extends Annotation> annotation) {
    return getAnnotatedElements(
        beanType, annotation, element -> ElementKind.FIELD.equals(element.getKind()));
  }

  /**
   * Returns all fields annotated with an annotation
   *
   * @param beanType the type to search in
   * @param annotation the target annotation
   * @return list of fields that are annotated with the annotation
   */
  public Optional<? extends Element> getFirstAnnotatedField(
      TypeMirror beanType, Class<? extends Annotation> annotation) {
    return findFirstAnnotatedElement(
        beanType, annotation, element -> ElementKind.FIELD.equals(element.getKind()));
  }

  /**
   * Returns all elements annotated with an annotation based on a filter
   *
   * @param beanType the type to search in
   * @param annotation the target annotation
   * @return list of the elements that are annotated with the annotation
   */
  public List<Element> getAnnotatedElements(
      TypeMirror beanType,
      Class<? extends Annotation> annotation,
      Function<Element, Boolean> filter) {
    TypeElement typeElement = (TypeElement) env.types().asElement(beanType);

    List<Element> annotatedMethods = getAnnotatedElements(typeElement, annotation, filter);
    return new ArrayList<>(annotatedMethods);
  }

  /**
   * Returns all elements annotated with an annotation based on a filter
   *
   * @param beanType the type to search in
   * @param annotation the target annotation
   * @return list of the elements that are annotated with the annotation
   */
  public Optional<? extends Element> findFirstAnnotatedElement(
      TypeMirror beanType,
      Class<? extends Annotation> annotation,
      Function<Element, Boolean> filter) {
    TypeElement typeElement = (TypeElement) env.types().asElement(beanType);

    return findFirstAnnotatedElement(typeElement, annotation, filter);
  }

  /**
   * Returns all elements annotated with an annotation based on a filter
   *
   * @param typeElement the element to search in
   * @param annotation the target annotation
   * @return list of the elements that are annotated with the annotation
   */
  public List<Element> getAnnotatedElements(
      TypeElement typeElement,
      Class<? extends Annotation> annotation,
      Function<Element, Boolean> filter) {
    TypeMirror superclass = typeElement.getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return new ArrayList<>();
    }

    List<Element> methods =
        typeElement.getEnclosedElements().stream()
            .filter(filter::apply)
            .filter(element -> nonNull(element.getAnnotation(annotation)))
            .collect(Collectors.toList());

    methods.addAll(
        getAnnotatedElements((TypeElement) env.types().asElement(superclass), annotation, filter));
    return methods;
  }

  /**
   * Returns all elements annotated with an annotation based on a filter
   *
   * @param typeElement the element to search in
   * @param annotation the target annotation
   * @return list of the elements that are annotated with the annotation
   */
  public Optional<? extends Element> findFirstAnnotatedElement(
      TypeElement typeElement,
      Class<? extends Annotation> annotation,
      Function<Element, Boolean> filter) {
    TypeMirror superclass = typeElement.getSuperclass();
    if (superclass.getKind().equals(TypeKind.NONE)) {
      return Optional.empty();
    }

    Optional<? extends Element> result =
        typeElement.getEnclosedElements().stream()
            .filter(filter::apply)
            .filter(element -> nonNull(element.getAnnotation(annotation)))
            .findFirst();

    if (result.isPresent()) {
      return result;
    }

    return findFirstAnnotatedElement(
        (TypeElement) env.types().asElement(superclass), annotation, filter);
  }

  public static void errorStackTrace(Messager messager, Exception e) {
    StringWriter out = new StringWriter();
    e.printStackTrace(new PrintWriter(out));
    messager.printMessage(
        Diagnostic.Kind.ERROR, "error while creating source file " + out.getBuffer().toString());
  }
}
