package cern.molr.commons.annotations;

import com.sun.tools.javac.code.Symbol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * A processor annotation, which is automatically plugged into javac
 * (see META-INF/services/javax.annotation.processing.Processor).
 * It recognizes {@link SourceMe} annotations and create a companion class containing the source code
 * (encoded in BASE64) of the annotated class.
 *
 * @author mgalilee
 */
@SupportedAnnotationTypes("cern.molr.commons.annotations.SourceMe")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SourceMeProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(SourceMe.class)) {
            if (elem.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) elem;
                if (classElement instanceof Symbol.ClassSymbol) {
                    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) classElement;
                    JavaFileObject javaFileObject;
                    try (BufferedReader reader = new BufferedReader(classSymbol.sourcefile.openReader(true))){
                        String content = reader.lines().collect(Collectors.joining("\n"));
                        String base64Content = Base64.getEncoder().encodeToString(content.getBytes());

                        javaFileObject = processingEnv.getFiler()
                                .createSourceFile(classElement.getQualifiedName() + "Source");
                        BufferedWriter bufferedWriter = new BufferedWriter(javaFileObject.openWriter());
                        if(!classSymbol.packge().isUnnamed()) {
                            bufferedWriter.append("package ");
                            bufferedWriter.append(classSymbol.packge().fullname);
                            bufferedWriter.append(";\n");
                        }
                        bufferedWriter.append("public class ");
                        bufferedWriter.append(classSymbol.getSimpleName());
                        bufferedWriter.append("Source implements cern.molr.commons.annotations.Source {\n");
                        bufferedWriter.append("  public final String base64Value() {\n    return \"");
                        bufferedWriter.append(base64Content);
                        bufferedWriter.append("\";\n  }\n}");
                        bufferedWriter.close();

                    } catch (IOException e) {
                        System.out.println(String.format("Exception while processing %s", classSymbol.className()));
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(String.format("Skipped %s as not an instance of ClassSymbol", classElement));
                }
            }
        }
        return true;
    }

}
