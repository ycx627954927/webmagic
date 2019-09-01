package com.yucx.test01;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;

/**
 * @descrpiton:
 * @author: changxing.yu
 * @date: 2019/9/2
 */
public class MonitorExample {
    public static void main(String[] args) throws Exception {
        Spider githubSpider = Spider.create(new GithubRepoPageProcessor())
                .addUrl("https://github.com/code4craft");

        SpiderMonitor.instance().register(githubSpider);
        githubSpider.start();
    }
}