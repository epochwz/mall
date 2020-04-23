window.$docsify = {
    // 项目信息
    name: 'docsify-plus',
    repo: 'https://github.com/epochwz/docsify-plus',
    // 主页
    homepage: 'README.md',
    // 封面
    coverpage: 'cover.md',
    onlyCover: true,
    // 导航栏
    loadNavbar: 'navbar.md',
    mergeNavbar: true,
    // 侧边栏
    loadSidebar: 'sidebar.md',
    subMaxLevel: 4,
    autoHeader: true,
    // 启用相对路径
    relativePath: true,
    // 路由别名
    alias: {
        '/docs/(.*)': '/$1'
    },
    // 切换页面时自动跳转回页面顶部
    auto2top: true,
    // 格式化文章更新时间
    formatUpdated: '{YYYY}-{MM}-{DD} {HH}:{mm}:{ss}',
    // 全文搜索
    search: {
        maxAge: 86400000, // 过期时间，单位毫秒，默认一天
        depth: 4, // 搜索标题的最大层级
        paths: 'auto',
        placeholder: 'search',
        noData: 'Not Found!'
    },
    // 自定义插件
    plugins: [
        function (hook, vm) {
            let url = window.$docsify.basePath ? window.$docsify.basePath : window.location.href.split("#")[0];
            hook.beforeEach(function (content) {
                // 站内图片链接自动转换：支持在 Markdown 中使用项目根路径作为图片引用的相对链接
                // markdown:   /docs/images/xxx.png
                // html:       http://host:port/path/images/xxx.png
                content = content.replace(/([:(]\s*)\/docs\/(images)/g, "$1" + url + "$2");

                // 兼容(自动转换) docsify 语法
                // 引用内容强调：支持在 Markdown 中使用 "> ! " 进行引用内容的强调；原始语法 "!> "
                content = content.replace(/([\r\n])> ! /g, "$1!> ");

                // 在文章标题下面追加水平分割线
                content = content.replace(/\s*(^# .*[\r\n]+)/g, "$1\r\n---\r\n");

                return content;
            });
        }
    ]
};

const footerHtml = [
    '<div id="app"></div>',
    '<script src="//unpkg.com/docsify/lib/docsify.min.js" data-ga="UA-155156951-1"></script>',
    '<!-- Google Analytics -->',
    '<script src="//unpkg.com/docsify/lib/plugins/ga.js"></script>',
    '<!-- 全文搜索 -->',
    '<script src="//unpkg.com/docsify/lib/plugins/search.min.js"></script>',
    '<!-- 图片缩放 -->',
    '<script src="//unpkg.com/docsify/lib/plugins/zoom-image.js"></script>',
    '<!-- 分页导航 -->',
    '<script src="//unpkg.com/docsify-pagination/dist/docsify-pagination.min.js"></script>',
    '<!-- 回到顶部 -->',
    // '<script src="//unpkg.com/docsify-scroll-to-top/dist/docsify-scroll-to-top.min.js"></script>',
    '<!-- 代码复制 -->',
    '<script src="//unpkg.com/docsify-copy-code@2"></script>',
    '<!-- 代码高亮 -->',
    '<script src="//unpkg.com/prismjs/components/prism-bash.min.js"></script>',
    '<script src="//unpkg.com/prismjs/components/prism-json.min.js"></script>',
    '<script src="//unpkg.com/prismjs/components/prism-java.min.js"></script>',
    '<script src="//unpkg.com/prismjs/components/prism-javascript.min.js"></script>',
    '<script src="//unpkg.com/prismjs/components/prism-sql.min.js"></script>',
    '<script src="//unpkg.com/prismjs/components/prism-css.min.js"></script>',
].join('');

document.write(footerHtml);