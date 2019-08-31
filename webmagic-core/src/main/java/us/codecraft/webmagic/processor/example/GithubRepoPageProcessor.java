package us.codecraft.webmagic.processor.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.3.2
 */
public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000);

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    @Override
    public void process(Page page) {
        /**
         * 对于下载到的Html页面，你如何从中抽取到你想要的信息？
         * WebMagic里主要使用了三种抽取技术：XPath、正则表达式和CSS选择器。
         * 另外，对于JSON格式的内容，可使用JsonPath进行解析。
         *
         * XPath
             XPath本来是用于XML中获取元素的一种查询语言，但是用于Html也是比较方便的。例如：
                page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()")
                这段代码使用了XPath，它的意思是“查找所有class属性为'entry-title public'的h1元素，
                并找到他的strong子节点的a子节点，并提取a节点的文本信息”。

         CSS选择器
            CSS选择器是与XPath类似的语言。如果大家做过前端开发，肯定知道$('h1.entry-title')这种写法的含义。
            客观的说，它比XPath写起来要简单一些，但是如果写复杂一点的抽取规则，就相对要麻烦一点。

         正则表达式
             正则表达式则是一种通用的文本抽取语言。
             page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
             这段代码就用到了正则表达式，它表示匹配所有"https://github.com/code4craft/webmagic"这样的链接。

         JsonPath
            JsonPath是于XPath很类似的一个语言，它用于从Json中快速定位一条内容。
            WebMagic中使用的JsonPath格式可以参考这里：https://code.google.com/p/json-path/
         */
        // 部分二：定义如何抽取页面信息，并保存下来
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        /**
         * 这段代码的分为两部分，page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all()
         * 用于获取所有满足"(https:/ /github\.com/\w+/\w+)"这个正则表达式的链接，
         * page.addTargetRequests()则将这些链接加入到待抓取的队列中去
         */
        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-])").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor())
                // 从"https://github.com/code4craft"开始抓
                .addUrl("https://github.com/code4craft")
                //开启5个线程抓取
                .thread(5)
                .run();
    }
}
