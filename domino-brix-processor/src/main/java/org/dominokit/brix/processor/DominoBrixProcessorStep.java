/*
 * #%L
 * gwt-websockets-processor
 * %%
 * Copyright (C) 2011 - 2018 Vertispan LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.dominokit.brix.processor;

import static java.util.Objects.nonNull;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dagger.Binds;
import dagger.Lazy;
import dagger.Module;
import dagger.multibindings.IntoSet;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.dominokit.brix.Brix;
import org.dominokit.brix.CoreComponent;
import org.dominokit.brix.PresentationModule;
import org.dominokit.brix.annotations.BrixModule;
import org.dominokit.brix.annotations.BrixPresenter;
import org.dominokit.brix.annotations.BrixRoute;
import org.dominokit.brix.annotations.BrixRoutingTask;
import org.dominokit.brix.annotations.BrixSlot;
import org.dominokit.brix.annotations.BrixTask;
import org.dominokit.brix.annotations.FragmentParameter;
import org.dominokit.brix.annotations.Global;
import org.dominokit.brix.annotations.Handlers;
import org.dominokit.brix.annotations.ListenFor;
import org.dominokit.brix.annotations.OnActivated;
import org.dominokit.brix.annotations.OnBeforeReveal;
import org.dominokit.brix.annotations.OnDeactivated;
import org.dominokit.brix.annotations.OnRemove;
import org.dominokit.brix.annotations.OnReveal;
import org.dominokit.brix.annotations.OnStateChanged;
import org.dominokit.brix.annotations.PathParameter;
import org.dominokit.brix.annotations.QueryParameter;
import org.dominokit.brix.annotations.RegisterSlots;
import org.dominokit.brix.annotations.Task;
import org.dominokit.brix.annotations.UiHandler;
import org.dominokit.brix.annotations.UiView;
import org.dominokit.brix.api.AbstractChildRoutingTask;
import org.dominokit.brix.api.AbstractRoutingTask;
import org.dominokit.brix.api.BrixComponentInitializer;
import org.dominokit.brix.api.ChildPresenter;
import org.dominokit.brix.api.Presenter;
import org.dominokit.brix.api.RoutingTask;
import org.dominokit.brix.api.Slot;
import org.dominokit.brix.api.StartupTask;
import org.dominokit.brix.api.UiHandlers;
import org.dominokit.brix.api.Viewable;
import org.dominokit.brix.events.BrixEvent;
import org.dominokit.brix.security.Authorizer;
import org.dominokit.brix.security.DenyAllAuthorizer;
import org.dominokit.brix.security.PermitAllAuthorizer;
import org.dominokit.brix.security.RolesAllowedAuthorizer;
import org.dominokit.domino.history.AppHistory;

public class DominoBrixProcessorStep implements BasicAnnotationProcessor.Step, HasProcessorEnv {

  private final ProcessingEnvironment processingEnv;
  private final SourceUtil sourceUtil;

  public DominoBrixProcessorStep(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
    this.sourceUtil = new SourceUtil(this);
  }

  @Override
  public Set<String> annotations() {
    return new HashSet<>(
        Arrays.asList(
            BrixModule.class.getCanonicalName(),
            Task.class.getCanonicalName(),
            BrixPresenter.class.getCanonicalName(),
            UiView.class.getCanonicalName()));
  }

  @Override
  public Set<? extends Element> process(
      ImmutableSetMultimap<String, Element> elementsByAnnotation) {
    messager().printMessage(Diagnostic.Kind.NOTE, "==BRIX : processing modules :->");
    try {
      processModule(elementsByAnnotation);
    } catch (Exception e) {
      SourceUtil.errorStackTrace(messager(), e);
    }

    return Sets.newHashSet();
  }

  private void processModule(ImmutableSetMultimap<String, Element> elementsByAnnotation) {
    elementsByAnnotation
        .get(BrixModule.class.getCanonicalName())
        .forEach(
            element -> {
              BrixModule brixModule = element.getAnnotation(BrixModule.class);
              String moduleName = brixModule.value();

              TypeSpec.Builder bindingModule =
                  TypeSpec.interfaceBuilder(
                      "Brix" + sourceUtil.capitalizeFirstLetter(moduleName) + "BindingModule_");
              bindingModule.addModifiers(Modifier.PUBLIC).addAnnotation(Module.class);

              TypeSpec.Builder superModule =
                  TypeSpec.classBuilder(
                      "Brix" + sourceUtil.capitalizeFirstLetter(moduleName) + "Module_");
              superModule
                  .addModifiers(Modifier.PUBLIC)
                  .addAnnotation(Module.class)
                  .addSuperinterface(TypeName.get(PresentationModule.class))
                  .addField(
                      FieldSpec.builder(
                              TypeName.get(CoreComponent.class), "coreComponent", Modifier.PRIVATE)
                          .build())
                  .addMethod(
                      MethodSpec.constructorBuilder()
                          .addAnnotation(Inject.class)
                          .addModifiers(Modifier.PUBLIC)
                          .addParameter(
                              ParameterSpec.builder(
                                      TypeName.get(CoreComponent.class), "coreComponent")
                                  .build())
                          .addStatement("this.coreComponent = coreComponent")
                          .build())
                  .addMethod(
                      MethodSpec.methodBuilder("coreComponent")
                          .addAnnotation(Override.class)
                          .addModifiers(Modifier.PUBLIC)
                          .returns(CoreComponent.class)
                          .addStatement("return this.coreComponent")
                          .build());

              TypeSpec typeSpec = generateStartupTasks(moduleName, elementsByAnnotation);
              writeFile(typeSpec, element);

              elementsByAnnotation
                  .get(BrixPresenter.class.getCanonicalName())
                  .forEach(
                      presenter -> {
                        TypeSpec.Builder presenterBuilder =
                            TypeSpec.classBuilder(presenter.getSimpleName().toString() + "Impl");
                        presenterBuilder
                            .addAnnotation(Singleton.class)
                            .superclass(TypeName.get(presenter.asType()))
                            .addModifiers(Modifier.PUBLIC);

                        addViewField(presenter, presenterBuilder);

                        copyQualifiers(presenter, presenterBuilder);
                        copyConstructors(presenter, presenterBuilder);
                        generateUiHandlers(presenter, presenterBuilder);
                        generateRouteMethod(presenter, presenterBuilder);
                        generateSlotMethod(presenter, presenterBuilder);
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, PostConstruct.class, "postConstruct");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnReveal.class, "onRevealed");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnRemove.class, "onRemoved");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnBeforeReveal.class, "onBeforeRevealed");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnActivated.class, "onActivated");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnDeactivated.class, "onDeactivated");
                        collectAnnotatedMethods(
                            presenter, presenterBuilder, OnStateChanged.class, "onStateChanged");
                        generateStateMethod(presenter, presenterBuilder);
                        generateEventsMethod(presenter, presenterBuilder);
                        generateRegisterSlotsMethod(presenter, presenterBuilder);
                        generateAuthorizerMethod(presenter, presenterBuilder);

                        writeFile(presenterBuilder.build(), presenter);
                        generatePresenterBinding(presenter, bindingModule);
                        generateRoutingTask(presenter, bindingModule);
                      });

              elementsByAnnotation.get(UiView.class.getCanonicalName()).stream()
                  .filter(e -> ElementKind.FIELD != e.getKind())
                  .forEach(
                      uiView -> {
                        TypeSpec.Builder builder =
                            TypeSpec.classBuilder(uiView.getSimpleName().toString() + "_UiView");
                        copyQualifiers(uiView, builder);
                        builder
                            .addModifiers(Modifier.PUBLIC)
                            .superclass(TypeName.get(uiView.asType()));
                        addUIHandlersField(uiView, builder);
                        copyConstructors(uiView, builder);

                        writeFile(builder.build(), uiView);
                        generateViewBinding(uiView, bindingModule);
                      });

              sourceUtil
                  .getClassValueFromAnnotation(element, BrixModule.class, "component")
                  .ifPresent(
                      component -> {
                        if (!MoreTypes.isTypeOf(BrixModule.NoneComponent.class, component)) {
                          TypeSpec.Builder componentService =
                              TypeSpec.classBuilder(
                                      "Brix"
                                          + sourceUtil.capitalizeFirstLetter(moduleName)
                                          + "ComponentService_")
                                  .addAnnotation(
                                      AnnotationSpec.builder(
                                              com.google.auto.service.AutoService.class)
                                          .addMember(
                                              "value", "$T.class", BrixComponentInitializer.class)
                                          .build())
                                  .addModifiers(Modifier.PUBLIC)
                                  .addSuperinterface(BrixComponentInitializer.class)
                                  .addMethod(
                                      MethodSpec.methodBuilder("init")
                                          .addAnnotation(Override.class)
                                          .addModifiers(Modifier.PUBLIC)
                                          .returns(TypeName.VOID)
                                          .addStatement(
                                              "$T.get().register($T.getInstance().module())",
                                              Brix.class,
                                              component)
                                          .build());
                          writeFile(componentService.build(), element);
                        }
                      });

              writeFile(bindingModule.build(), element);
              writeFile(superModule.build(), element);
            });
  }

  private static void copyQualifiers(Element element, TypeSpec.Builder builder) {
    Set<? extends AnnotationMirror> qualifiers = getQualifiers(element);

    qualifiers.forEach(
        annotationMirror -> {
          builder.addAnnotation(
              ClassName.get((TypeElement) annotationMirror.getAnnotationType().asElement()));
        });
  }

  private static Set<? extends AnnotationMirror> getQualifiers(Element element) {
    return element.getAnnotationMirrors().stream()
        .filter(
            annotationMirror ->
                nonNull(
                    annotationMirror
                        .getAnnotationType()
                        .asElement()
                        .getAnnotation(Qualifier.class)))
        .collect(Collectors.toSet());
  }

  private void addViewField(Element presenter, TypeSpec.Builder presenterBuilder) {
    Optional<? extends Element> uiViewField =
        sourceUtil.getFirstAnnotatedField(presenter.asType(), UiView.class);
    Optional<? extends TypeMirror> viewInterface =
        sourceUtil.findTypeArgument(presenter, Viewable.class);
    TypeName viewType =
        viewInterface.isPresent()
            ? TypeName.get(viewInterface.get())
            : TypeName.get(Viewable.class);
    if (uiViewField.isPresent()) {
      Element viewElement = uiViewField.get();
      boolean isLazy = sourceUtil.isAssignableFrom(viewElement.asType(), Lazy.class);
      MethodSpec.Builder getView =
          MethodSpec.methodBuilder("view")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(viewType)
              .addStatement(
                  "return $T.ofNullable(super.view())\n" + "        .orElse($L$L())",
                  Optional.class,
                  viewElement.getSimpleName().toString(),
                  (isLazy ? ".get" : ""));
      presenterBuilder.addMethod(getView.build());
    } else {
      FieldSpec.Builder lazyViewField =
          FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Lazy.class), viewType), "view")
              .addAnnotation(Inject.class);

      MethodSpec.Builder getView =
          MethodSpec.methodBuilder("view")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(viewType)
              .addStatement(
                  "return $T.ofNullable(super.view())\n" + "        .orElse($L.get())",
                  Optional.class,
                  "view");

      presenterBuilder.addField(lazyViewField.build());
      presenterBuilder.addMethod(getView.build());
    }
  }

  private void copyConstructors(Element source, TypeSpec.Builder builder) {
    List<? extends ExecutableElement> elements =
        source.getEnclosedElements().stream()
            .filter(element -> ElementKind.CONSTRUCTOR == element.getKind())
            .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE))
            .map(element -> (ExecutableElement) element)
            .collect(Collectors.toList());

    if (elements.isEmpty() || (elements.size() == 1 && elements.get(0).getParameters().isEmpty())) {
      builder.addMethod(MethodSpec.constructorBuilder().addAnnotation(Inject.class).build());
    } else {
      elements.forEach(
          element -> {
            MethodSpec.Builder constructor =
                MethodSpec.constructorBuilder().addModifiers(element.getModifiers());
            element
                .getThrownTypes()
                .forEach(typeMirror -> constructor.addException(TypeName.get(typeMirror)));
            element
                .getParameters()
                .forEach(
                    param -> {
                      ParameterSpec.Builder paranBuilder =
                          ParameterSpec.builder(
                              TypeName.get(param.asType()), param.getSimpleName().toString());

                      param
                          .getAnnotationMirrors()
                          .forEach(
                              annotationMirror ->
                                  paranBuilder.addAnnotation(
                                      ClassName.get(
                                          (TypeElement)
                                              annotationMirror.getAnnotationType().asElement())));
                      constructor.addParameter(paranBuilder.build());
                    });
            constructor.addStatement(
                "super($L)",
                element.getParameters().stream()
                    .map(p -> p.getSimpleName().toString())
                    .collect(Collectors.joining(",")));

            element
                .getAnnotationMirrors()
                .forEach(
                    annotationMirror ->
                        constructor.addAnnotation(
                            ClassName.get(
                                (TypeElement) annotationMirror.getAnnotationType().asElement())));

            builder.addMethod(constructor.build());
          });
    }
  }

  private void addUIHandlersField(Element uiView, TypeSpec.Builder viewBuilder) {
    Optional<? extends Element> uiHandlersField =
        sourceUtil.getFirstAnnotatedField(uiView.asType(), Handlers.class);
    Optional<? extends TypeMirror> uiHandlersInterface =
        sourceUtil.findTypeArgument(uiView, UiHandlers.class);
    TypeName handlersType =
        uiHandlersInterface.isPresent()
            ? TypeName.get(uiHandlersInterface.get())
            : TypeName.get(UiHandlers.class);
    if (uiHandlersField.isPresent()) {
      Element handlersElement = uiHandlersField.get();
      boolean isLazy = sourceUtil.isAssignableFrom(handlersElement.asType(), Lazy.class);
      MethodSpec.Builder getView =
          MethodSpec.methodBuilder("handlers")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(handlersType)
              .addStatement(
                  "return $T.ofNullable(super.handlers())\n" + "        .orElse($L$L())",
                  Optional.class,
                  handlersElement.getSimpleName().toString(),
                  (isLazy ? ".get" : ""));
      viewBuilder.addMethod(getView.build());
    } else {
      FieldSpec.Builder lazyHandlersField =
          FieldSpec.builder(
                  ParameterizedTypeName.get(ClassName.get(Lazy.class), handlersType), "handlers")
              .addAnnotation(Inject.class);

      MethodSpec.Builder getHandlers =
          MethodSpec.methodBuilder("handlers")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(handlersType)
              .addStatement(
                  "return $T.ofNullable(super.handlers())\n" + "        .orElse($L.get())",
                  Optional.class,
                  "handlers");

      viewBuilder.addField(lazyHandlersField.build());
      viewBuilder.addMethod(getHandlers.build());
    }
  }

  private void generateRouteMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    BrixRoute route = sourceUtil.findClassAnnotation(presenter, BrixRoute.class);
    if (nonNull(route) && nonNull(route.value()) && !route.value().trim().isEmpty()) {
      presenterBuilder.addMethod(
          MethodSpec.methodBuilder("getRoutingPath")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .returns(String.class)
              .addStatement("return $S", route.value())
              .build());
    }
  }

  private void generateSlotMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    BrixSlot slot = sourceUtil.findClassAnnotation(presenter, BrixSlot.class);
    if (nonNull(slot) && nonNull(slot.value()) && !slot.value().trim().isEmpty()) {
      presenterBuilder.addMethod(
          MethodSpec.methodBuilder("getSlotKey")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(String.class)
              .addStatement("return $S", slot.value())
              .build());
    }
  }

  private void generateUiHandlers(Element presenter, TypeSpec.Builder presenterBuilder) {
    List<Element> methods = sourceUtil.getAnnotatedMethods(presenter.asType(), UiHandler.class);

    String name = presenter.getSimpleName().toString() + "UiHandlers";
    TypeSpec.Builder builder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
    methods.stream()
        .map(element -> (ExecutableElement) element)
        .forEach(
            method -> {
              MethodSpec.Builder interfaceMethod =
                  MethodSpec.methodBuilder(method.getSimpleName().toString())
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                      .returns(TypeName.get(method.getReturnType()));

              method
                  .getParameters()
                  .forEach(
                      variableElement -> {
                        interfaceMethod.addParameter(
                            ParameterSpec.builder(
                                    TypeName.get(variableElement.asType()),
                                    variableElement.getSimpleName().toString())
                                .build());
                      });
              builder.addMethod(interfaceMethod.build());
            });
    processingEnv
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "======= > Writing uiHandlers : " + name);
    writeFile(builder.build(), presenter);
  }

  private void collectAnnotatedMethods(
      Element presenter,
      TypeSpec.Builder presenterBuilder,
      Class<? extends Annotation> annotation,
      String methodName) {
    List<Element> methods = sourceUtil.getAnnotatedMethods(presenter.asType(), annotation);
    if (nonNull(methods) && !methods.isEmpty()) {
      MethodSpec.Builder builder =
          MethodSpec.methodBuilder(methodName)
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .returns(TypeName.VOID);
      builder.addStatement("super." + methodName + "()");
      methods.forEach(element -> builder.addStatement(element.getSimpleName().toString() + "()"));
      presenterBuilder.addMethod(builder.build());
    }
  }

  private void generateRegisterSlotsMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    if (nonNull(sourceUtil.findClassAnnotation(presenter, RegisterSlots.class))) {
      List<String> slots =
          Arrays.asList(sourceUtil.findClassAnnotation(presenter, RegisterSlots.class).value());
      if (!slots.isEmpty()) {
        TypeSpec.Builder slotsInterfaceBuilder =
            TypeSpec.interfaceBuilder(
                    presenter.getSimpleName().toString().replace("Presenter", "") + "Slots")
                .addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder method =
            MethodSpec.methodBuilder("registerSlots")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(TypeName.VOID)
                .addStatement("super.registerSlots()");
        slots.forEach(
            slotName -> {
              method.addStatement(
                  "registerSlot(view().$L($S))", slotAsMethodName(slotName), slotName);
              slotsInterfaceBuilder.addMethod(
                  MethodSpec.methodBuilder(slotAsMethodName(slotName))
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                      .returns(TypeName.get(Slot.class))
                      .addParameter(ParameterSpec.builder(String.class, "key").build())
                      .build());
            });
        presenterBuilder.addMethod(method.build());
        writeFile(slotsInterfaceBuilder.build(), presenter);
      }
    }
  }

  private void generateAuthorizerMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    PermitAll permitAll = sourceUtil.findClassAnnotation(presenter, PermitAll.class);
    DenyAll denyAll = sourceUtil.findClassAnnotation(presenter, DenyAll.class);
    RolesAllowed rolesAllowed = sourceUtil.findClassAnnotation(presenter, RolesAllowed.class);

    long count = Stream.of(permitAll, denyAll, rolesAllowed).filter(Objects::nonNull).count();
    boolean mixed = count > 1;

    if (mixed) {
      messager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Only one of [PermitAll, DenyAll, RolesAllowed] is allowed on a presenter.",
              presenter);
    }

    MethodSpec.Builder authorizerMethod =
        MethodSpec.methodBuilder("getAuthorizer")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(Authorizer.class);

    if (nonNull(permitAll)) {
      authorizerMethod.addStatement("return $T.INSTANCE", PermitAllAuthorizer.class);
    }

    if (nonNull(denyAll)) {
      authorizerMethod.addStatement("return $T.INSTANCE", DenyAllAuthorizer.class);
    }

    if (nonNull(rolesAllowed)) {
      authorizerMethod.addStatement("return $T.INSTANCE", RolesAllowedAuthorizer.class);
      MethodSpec.Builder rolesMethod =
          MethodSpec.methodBuilder("getRoles")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .returns(ParameterizedTypeName.get(Set.class, String.class))
              .addStatement("$T<$T> roles = new $T<>()", Set.class, String.class, HashSet.class);

      if (nonNull(rolesAllowed.value())) {
        Arrays.asList(rolesAllowed.value())
            .forEach(
                role -> {
                  rolesMethod.addStatement("roles.add($S)", role);
                });
      }

      rolesMethod.addStatement("return roles");
      presenterBuilder.addMethod(rolesMethod.build());
    }

    if (count > 0) {
      presenterBuilder.addMethod(authorizerMethod.build());
    }
  }

  public String slotAsMethodName(String slotName) {
    String methodName =
        Arrays.stream(slotName.split("-|\\."))
            .map(sourceUtil::capitalizeFirstLetter)
            .collect(Collectors.joining());
    return "get" + methodName + "Slot";
  }

  public String slotAsFieldName(String slotName) {
    String fieldName =
        Arrays.stream(slotName.split("-|\\."))
            .map(String::toUpperCase)
            .collect(Collectors.joining("_"));
    return fieldName;
  }

  private void generateEventsMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    List<Element> methods = sourceUtil.getAnnotatedMethods(presenter.asType(), ListenFor.class);
    if (nonNull(methods) && !methods.isEmpty()) {
      MethodSpec.Builder builder =
          MethodSpec.methodBuilder("onEventReceived")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .returns(TypeName.VOID)
              .addParameter(ParameterSpec.builder(TypeName.get(BrixEvent.class), "event").build());
      if (!methods.isEmpty()) {
        builder.addStatement("super.onEventReceived(event)");
      }
      methods.forEach(
          element -> {
            Optional<TypeMirror> eventType =
                sourceUtil.getClassValueFromAnnotation(element, ListenFor.class, "value");
            eventType.ifPresent(
                typeMirror -> {
                  CodeBlock.Builder code = CodeBlock.builder();
                  code.beginControlFlow("if(event.isSameType($T.class))", typeMirror);
                  code.add("$L(($T)event);\n", element.getSimpleName().toString(), typeMirror);
                  code.endControlFlow();
                  builder.addStatement(code.build());
                });
          });
      presenterBuilder.addMethod(builder.build());
    }
  }

  private void generateStateMethod(Element presenter, TypeSpec.Builder presenterBuilder) {
    List<Element> pathParameters =
        sourceUtil.getAnnotatedFields(presenter.asType(), PathParameter.class);
    List<Element> fragmentParameters =
        sourceUtil.getAnnotatedFields(presenter.asType(), FragmentParameter.class);
    List<Element> queryParameters =
        sourceUtil.getAnnotatedFields(presenter.asType(), QueryParameter.class);

    if (!pathParameters.isEmpty() || !fragmentParameters.isEmpty() || !queryParameters.isEmpty()) {

      MethodSpec.Builder stateMethod =
          MethodSpec.methodBuilder("setState")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .returns(TypeName.VOID);

      pathParameters.forEach(
          paramElement -> {
            PathParameter annotation = paramElement.getAnnotation(PathParameter.class);
            String paramName =
                annotation.value().trim().isEmpty()
                    ? paramElement.getSimpleName().toString()
                    : annotation.value();
            stateMethod.addStatement(
                "this.$L = state.normalizedToken().getPathParameter($S)",
                paramElement.getSimpleName().toString(),
                paramName);
          });

      fragmentParameters.forEach(
          fragmentElement -> {
            FragmentParameter annotation = fragmentElement.getAnnotation(FragmentParameter.class);
            String paramName =
                annotation.value().trim().isEmpty()
                    ? fragmentElement.getSimpleName().toString()
                    : annotation.value();
            stateMethod.addStatement(
                "this.$L = state.normalizedToken().getFragmentParameter($S)",
                fragmentElement.getSimpleName().toString(),
                paramName);
          });

      queryParameters.forEach(
          queryParamElement -> {
            QueryParameter annotation = queryParamElement.getAnnotation(QueryParameter.class);
            String paramName =
                annotation.value().trim().isEmpty()
                    ? queryParamElement.getSimpleName().toString()
                    : annotation.value();
            stateMethod.addStatement(
                "this.$L = state.token().getQueryParameter($S)",
                queryParamElement.getSimpleName().toString(),
                paramName);
          });

      presenterBuilder.addMethod(stateMethod.build());
    }
  }

  private void generatePresenterBinding(Element presenter, TypeSpec.Builder bindingModule) {
    sourceUtil
        .findImplementedInterface(presenter, UiHandlers.class)
        .ifPresent(
            typeMirror -> {
              MethodSpec.Builder bindMethod =
                  MethodSpec.methodBuilder(
                          sourceUtil.smallFirstLetter(presenter.getSimpleName().toString()))
                      .addAnnotation(Singleton.class)
                      .addAnnotation(Binds.class)
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                      .returns(TypeName.get(typeMirror))
                      .addParameter(
                          ClassName.bestGuess(presenter.asType().toString() + "Impl"), "impl");
              getQualifiers(presenter)
                  .forEach(
                      annotationMirror ->
                          bindMethod.addAnnotation(
                              ClassName.get(
                                  (TypeElement) annotationMirror.getAnnotationType().asElement())));
              bindingModule.addMethod(bindMethod.build());
            });
  }

  private void generateViewBinding(Element uiView, TypeSpec.Builder bindingModule) {
    sourceUtil
        .findImplementedInterface(uiView, Viewable.class)
        .ifPresent(
            typeMirror -> {
              MethodSpec.Builder bindMethod =
                  MethodSpec.methodBuilder(
                          sourceUtil.smallFirstLetter(uiView.getSimpleName().toString()))
                      .addAnnotation(Singleton.class)
                      .addAnnotation(Binds.class)
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                      .returns(TypeName.get(typeMirror))
                      .addParameter(
                          ClassName.bestGuess(uiView.asType().toString() + "_UiView"), "impl");

              getQualifiers(uiView)
                  .forEach(
                      annotationMirror ->
                          bindMethod.addAnnotation(
                              ClassName.get(
                                  (TypeElement) annotationMirror.getAnnotationType().asElement())));

              bindingModule.addMethod(bindMethod.build());
            });
  }

  private void generateRoutingTask(Element presenter, TypeSpec.Builder bindingModule) {
    boolean childPresenter = sourceUtil.isAssignableFrom(ChildPresenter.class, presenter.asType());
    if (childPresenter) {

      TypeMirror parentPresenter = sourceUtil.findTypeArgument(presenter, Presenter.class).get();

      TypeSpec.Builder routingTask =
          TypeSpec.classBuilder(presenter.getSimpleName().toString() + "Routing")
              .superclass(
                  ParameterizedTypeName.get(
                      ClassName.get(AbstractChildRoutingTask.class),
                      TypeName.get(parentPresenter),
                      TypeName.get(presenter.asType())));

      ParameterSpec.Builder parentParam =
          ParameterSpec.builder(ClassName.bestGuess(parentPresenter + "Impl"), "parent");
      Set<? extends AnnotationMirror> parentQualifiers =
          getQualifiers(types().asElement(parentPresenter));
      parentQualifiers.forEach(
          annotationMirror -> {
            parentParam.addAnnotation(
                ClassName.get((TypeElement) annotationMirror.getAnnotationType().asElement()));
          });

      Set<? extends AnnotationMirror> qualifiers = getQualifiers(presenter);

      ParameterSpec.Builder presenterParam =
          ParameterSpec.builder(
              ClassName.bestGuess(presenter.asType().toString() + "Impl"), "presenter");
      qualifiers.forEach(
          annotationMirror -> {
            presenterParam.addAnnotation(
                ClassName.get((TypeElement) annotationMirror.getAnnotationType().asElement()));
          });

      routingTask
          .addAnnotation(Singleton.class)
          .addModifiers(Modifier.PUBLIC)
          .addMethod(
              MethodSpec.constructorBuilder()
                  .addAnnotation(Inject.class)
                  .addModifiers(Modifier.PUBLIC)
                  .addParameter(parentParam.build())
                  .addParameter(presenterParam.build())
                  .addStatement("super(parent, presenter)")
                  .build());

      TypeSpec routingType = routingTask.build();
      writeFile(routingType, presenter);

      bindingModule.addMethod(
          MethodSpec.methodBuilder(
                  sourceUtil.smallFirstLetter(presenter.getSimpleName().toString() + "Routing"))
              .addAnnotation(Singleton.class)
              .addAnnotation(Binds.class)
              .addAnnotation(IntoSet.class)
              .addAnnotation(BrixRoutingTask.class)
              .returns(RoutingTask.class)
              .addParameter(
                  ClassName.bestGuess(
                      elements().getPackageOf(presenter).getQualifiedName().toString()
                          + "."
                          + presenter.getSimpleName().toString()
                          + "Routing"),
                  "routing")
              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
              .build());

    } else {
      TypeSpec.Builder routingTask =
          TypeSpec.classBuilder(presenter.getSimpleName().toString() + "Routing")
              .superclass(
                  ParameterizedTypeName.get(
                      ClassName.get(AbstractRoutingTask.class), TypeName.get(presenter.asType())));

      ParameterSpec.Builder presenterParam =
          ParameterSpec.builder(
              ClassName.bestGuess(presenter.asType().toString() + "Impl"), "presenter");

      Set<? extends AnnotationMirror> qualifiers = getQualifiers(presenter);
      qualifiers.forEach(
          annotationMirror -> {
            presenterParam.addAnnotation(
                ClassName.get((TypeElement) annotationMirror.getAnnotationType().asElement()));
          });

      routingTask
          .addAnnotation(Singleton.class)
          .addModifiers(Modifier.PUBLIC)
          .addMethod(
              MethodSpec.constructorBuilder()
                  .addAnnotation(Inject.class)
                  .addModifiers(Modifier.PUBLIC)
                  .addParameter(
                      ParameterSpec.builder(TypeName.get(AppHistory.class), "history")
                          .addAnnotation(Global.class)
                          .build())
                  .addParameter(presenterParam.build())
                  .addStatement("super(history, presenter)")
                  .build());

      TypeSpec routingType = routingTask.build();
      writeFile(routingType, presenter);

      bindingModule.addMethod(
          MethodSpec.methodBuilder(
                  sourceUtil.smallFirstLetter(presenter.getSimpleName().toString() + "Routing"))
              .addAnnotation(Singleton.class)
              .addAnnotation(Binds.class)
              .addAnnotation(IntoSet.class)
              .addAnnotation(BrixRoutingTask.class)
              .returns(RoutingTask.class)
              .addParameter(
                  ClassName.bestGuess(
                      elements().getPackageOf(presenter).getQualifiedName().toString()
                          + "."
                          + presenter.getSimpleName().toString()
                          + "Routing"),
                  "routing")
              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
              .build());
    }
  }

  private TypeSpec generateStartupTasks(
      String moduleName, ImmutableSetMultimap<String, Element> elementsByAnnotation) {

    TypeSpec.Builder tasksModule =
        TypeSpec.interfaceBuilder(
                "Brix" + sourceUtil.capitalizeFirstLetter(moduleName) + "TasksModule_")
            .addAnnotation(Module.class);
    elementsByAnnotation
        .get(Task.class.getCanonicalName())
        .forEach(
            taskElement -> {
              tasksModule.addMethod(
                  MethodSpec.methodBuilder(
                          sourceUtil.smallFirstLetter(taskElement.getSimpleName().toString()))
                      .addAnnotation(Singleton.class)
                      .addAnnotation(IntoSet.class)
                      .addAnnotation(Binds.class)
                      .addAnnotation(BrixTask.class)
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                      .addParameter(
                          ParameterSpec.builder(TypeName.get(taskElement.asType()), "task").build())
                      .returns(StartupTask.class)
                      .build());
            });
    return tasksModule.build();
  }

  private void writeFile(TypeSpec typeSpec, Element element) {
    try {
      JavaFile.builder(elements().getPackageOf(element).getQualifiedName().toString(), typeSpec)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      ExceptionUtil.messageStackTrace(processingEnv.getMessager(), e);
      processingEnv
          .getMessager()
          .printMessage(Diagnostic.Kind.ERROR, "Failed to write generated class", element);
    }
  }

  @Override
  public Types types() {
    return processingEnv.getTypeUtils();
  }

  @Override
  public Elements elements() {
    return processingEnv.getElementUtils();
  }

  @Override
  public Messager messager() {
    return processingEnv.getMessager();
  }
}
