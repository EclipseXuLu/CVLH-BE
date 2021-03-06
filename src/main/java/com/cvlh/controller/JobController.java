package com.cvlh.controller;

import com.cvlh.commons.base.BaseController;
import com.cvlh.model.NlpJob;
import com.cvlh.service.NlpJobService;
import com.cvlh.spider.NlpJobSpider;
import com.cvlh.util.Json2Btable;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class JobController extends BaseController {
    @Autowired
    private NlpJobService nlpJobService;

    @RequestMapping(value = "/hzau/job/crawl", method = RequestMethod.POST)
    @ResponseBody
    public Object crwalNlpJobs(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, String crawlType) throws IOException {
        nlpJobService.deleteByType(crawlType);
        List<NlpJob> nlpJobList = new NlpJobSpider().crawlJobInfo(crawlType);
        nlpJobService.batchInsertNlpJobs(nlpJobList);

        return renderSuccess("type " + crawlType + " has been crawled done!", httpServletResponse);
    }

    @RequestMapping(value = "/hzau/job/view", method = RequestMethod.GET)
    @ResponseBody
    public void viewNlpJobs(HttpServletResponse httpServletResponse, Integer offset, Integer limit, String jobType, String search, String sort, String order) throws IOException {
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        if (null != sort) {
            if (sort.equals("applynum")) {
                sort = "applyNum";
            }
            if (sort.equals("publishdate")) {
                sort = "publishDate";
            }
        }
        List<NlpJob> nlpJobList = nlpJobService.selectAll(jobType, search, sort, order);
        this.outJson2Btable(new Json2Btable(page.getTotal(), nlpJobList), httpServletResponse);
    }

    @RequestMapping(value = "/hzau/job/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteNlpJobs(HttpServletResponse httpServletResponse, Long id) throws IOException {
        nlpJobService.deleteByPrimaryKey(id);

        return renderSuccess("Delete successfully!", httpServletResponse);
    }
}
