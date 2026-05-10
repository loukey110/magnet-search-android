# 磁力搜索 Android App

一个基于规则引擎的磁力搜索Android应用。

## 技术栈

- **语言**: Kotlin
- **最低SDK**: Android 7.0 (API 24)
- **架构**: MVVM + Repository
- **网络请求**: OkHttp
- **HTML解析**: Jsoup
- **数据库**: Room
- **异步处理**: Coroutines + Flow

## 项目结构

```
app/src/main/java/com/magnet/search/
├── App.kt                          # Application类
├── MainActivity.kt                 # 主Activity
├── data/
│   ├── model/                      # 数据模型
│   │   ├── MagnetItem.kt           # 磁力条目
│   │   ├── SearchRule.kt           # 搜索规则
│   │   ├── Favorite.kt             # 收藏
│   │   └── SearchHistory.kt        # 搜索历史
│   ├── local/                      # 本地存储
│   │   ├── AppDatabase.kt          # Room数据库
│   │   ├── FavoriteDao.kt          # 收藏DAO
│   │   └── SearchHistoryDao.kt     # 历史DAO
│   └── remote/                     # 网络请求
│       ├── RuleParser.kt           # 规则解析器
│       └── DefaultRules.kt         # 默认规则配置
├── domain/
│   └── MagnetRepository.kt         # 数据仓库
├── ui/
│   ├── search/                     # 搜索模块
│   ├── favorites/                  # 收藏模块
│   ├── history/                    # 历史模块
│   └── adapter/                    # RecyclerView适配器
└── utils/                          # 工具类
```

## 核心功能

### 1. 规则引擎

支持通过JSON配置多个搜索源，每个规则包含：

```kotlin
SearchRule(
    id = "source_id",           // 唯一标识
    name = "来源名称",            // 显示名称
    baseUrl = "https://...",    // 基础URL
    searchUrl = "...",          // 搜索URL模板
    listSelector = "...",       // 列表选择器
    titleSelector = "...",      // 标题选择器
    magnetSelector = "...",     // 磁力链接选择器
    sizeSelector = "...",       // 大小选择器
    // ...
)
```

### 2. 多源聚合搜索

同时向多个数据源发起请求，聚合结果并排序展示。

### 3. 收藏与历史

- 支持收藏磁力链接
- 记录搜索历史
- 一键复制磁力链接

## 如何添加新的数据源

在 `DefaultRules.kt` 中添加新的规则：

```kotlin
SearchRule(
    id = "new_source",
    name = "新数据源",
    baseUrl = "https://example.com",
    searchUrl = "https://example.com/search?q={keyword}&p={page}",
    encoding = "UTF-8",
    listSelector = "div.result-item",
    titleSelector = "a.title",
    magnetSelector = "a.magnet",
    magnetAttr = "href",
    sizeSelector = "span.size"
)
```

## 构建项目

```bash
# 克隆项目
git clone <repo-url>
cd MagnetSearch

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease
```

## 注意事项

1. 数据源网站可能随时失效，建议使用规则引擎动态更新
2. 请遵守相关法律法规，仅用于学习研究
3. 部分网站需要特定的Headers或Cookie才能访问

## License

MIT License
