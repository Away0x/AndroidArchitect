# 项目说明

- 问题
    - 在大型项目中，使用 xml 定义路由的方式会造成代码耦合，不利于维护。需要改为注解标记路由节点
    - Navigation 切换 fragment 时使用的是 replace，会导致 fragment 生命周期的重启，需要改为 hide-show 的切换方式
- 解决
    - 摒弃 xml 文件，注解标记路由节点
    - 自定义 FragmentNavigator

##### 1. 自定义注解处理器
- gradle 配置

```
api 'com.alibaba:fastjson:1.2.59'
api 'com.google.auto.service:auto-service:1.0-rc6'\
annotationProcessor 'com.google.auto.service:auto-service:1.0-rc6'
```
- 注解处理器基本用法

```java
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.devio.as.hi.nav-annotation.FragmentDestination"})
public class NavProcessor extends AbstractProcessor {
    @Override void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv); //处理器被初始化的时候被调用
    }

    //处理器处理自定义注解的地方
    boolean process(Set annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
```
- 注解处理器的引用

```
kapt project(path:'nav-compiler')
api project(path:'nav-annotations')
```
- Java 中的几种 Element 类型

| Element | 作用域 |
| - | - |
| TypeElement | 代表该元素是一个类或者接口 |
| VariableElement | 代表该元素是字段，方法入参，枚举常量 |
| PackageElement | 代表该元素是包级别 |
| ExecutableElement | 代表方法，构造方法 |
| TypeParameterElement | 代表类型，接口，方法入参的泛型参数 |

- 自定义 fragment 导航器

```java
@Navigator.Name("HiFragment")
public class HiFragmentNavigator extends Navigator<HiFragmentNavigator.Destination> {

    public NavDestination navigate(Destination destination, Bundle args) {}

    class Destination extends NavDestination {
        public Destination(Navigator navigator) {}
    }

}
```