package com.away0x.com.router.nav_compiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.away0x.com.router.nav_annotation.Destination;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/** 注解处理器 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.away0x.com.router.nav_annotation.Destination"})
public class NavProcessor extends AbstractProcessor {

    private static final String PAGE_TYPE_ACTIVITY = "Activity";
    private static final String PAGE_TYPE_FRAGMENT = "Fragment";
    private static final String PAGE_TYPE_DIALOG = "Dialog";
    private static final String OUTPUT_FILE_NAME = "destination.json";

    // java library 中无法像 android library 中那样使用 log 打印日志
    // 所以这里使用 Messager 来输出日志
    private Messager messager;
    // 用于创建文件
    private Filer filer;

    // 处理器被初始化的时候调用
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        // 输出日志
        messager.printMessage(Diagnostic.Kind.NOTE, "enter init ...");
        // filer 用于操作文件
        filer = processingEnvironment.getFiler();
    }

    // 处理器处理自定义注解的地方
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 获取项目中所有使用了 Destination 注解的地方
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Destination.class);

        if (!elements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(elements, Destination.class, destMap);

            // 保存到文件
            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath(); // /app/build/intermediates/javac/debug/classes/目录下
                // 得到 app/main/assets/ 路径
                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assestPath = appPath + "src/main/assets";

                File file = new File(assestPath);
                if (file.exists()) {
                    file.mkdirs();
                }

                String content = JSON.toJSONString(destMap, true); // 转化为 json 字符串

                File outputFile = new File(assestPath, OUTPUT_FILE_NAME);
                if (outputFile.exists()) {
                    outputFile.delete();
                }

                outputFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.write(content);
                writer.flush();

                fileOutputStream.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /** 处理注解，转换为 json，保存到 destMap 中 */
    /**
    Java 中的几种 Element 类型

    | Element | 作用域 |
    | - | - |
    | TypeElement | 代表该元素是一个类或者接口 |
    | VariableElement | 代表该元素是字段，方法入参，枚举常量 |
    | PackageElement | 代表该元素是包级别 |
    | ExecutableElement | 代表方法，构造方法 |
    | TypeParameterElement | 代表类型，接口，方法入参的泛型参数 |

     */
    private void handleDestination(Set<? extends Element> elements, Class<Destination> destinationClass, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            // 全类名
            String clazName = typeElement.getQualifiedName().toString();
            // 获取注解以及注解的参数
            Destination annotation = typeElement.getAnnotation(destinationClass);
            String pageUrl = annotation.pageUrl();
            boolean asStarter = annotation.asStarter();
            int id = Math.abs(clazName.hashCode());

            // 获取 Destination 注解修饰的类的类型: Activity, Dialog, Fragment
            String destType = getDestinationType(typeElement);

            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl:" + pageUrl);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clazName", clazName);
                jsonObject.put("pageUrl", pageUrl);
                jsonObject.put("asStarter", asStarter);
                jsonObject.put("id", id);
                jsonObject.put("destType", destType);

                destMap.put(pageUrl, jsonObject);
            }
        }
    }

    /** 获取 Destination 注解修饰的类的类型: Activity, Dialog, Fragment */
    private String getDestinationType(TypeElement typeElement) {
        // 获取父类，比如 Fragment 会获取到: androidx.fragment.app.Fragment
        TypeMirror typeMirror = typeElement.getSuperclass();
        String superClazName = typeMirror.toString();

        if (superClazName.contains(PAGE_TYPE_ACTIVITY.toLowerCase())) {
            return PAGE_TYPE_ACTIVITY.toLowerCase();
        } else if (superClazName.contains(PAGE_TYPE_FRAGMENT.toLowerCase())) {
            return PAGE_TYPE_FRAGMENT.toLowerCase();
        } else if (superClazName.contains(PAGE_TYPE_DIALOG.toLowerCase())) {
            return PAGE_TYPE_DIALOG.toLowerCase();
        }

        // 这个父类的类型是类的类型，或者是接口的类型
        if (typeMirror instanceof DeclaredType) {
            Element element = ((DeclaredType) typeMirror).asElement();
            if (element instanceof TypeElement) {
                return getDestinationType((TypeElement) element);
            }
        }

        return null;
    }

}
