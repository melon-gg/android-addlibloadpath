# android-addlibloadpath
load android *.so from your own set path


用于android so的热更新，下载动态库so到指定目录然后进行加载，例如指定路径
/data/data/com.example.myapplication/files

必须使用System.load方法加载绝对路径的so，尝试使用System.loadLibrary加载失败

通过PathClassLoader获取系统库加载路径，然后追加上自己的库加载路径即可
