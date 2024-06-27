package cn.ankele.plugin.error;

// 自定义异常类，表示缺少前置插件的错误
public class PluginNotInstalledException extends Exception {
    public PluginNotInstalledException(String message) {
        super(message);
    }
}