package io.owslab.mailreceiver.controller;

import io.owslab.mailreceiver.dao.VariableDAO;
import io.owslab.mailreceiver.model.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by khanhlvb on 3/2/18.
 */
@Controller
@RequestMapping("/admin/")
public class HelpController {
    @Value("${mailreceiver.app.version}")
    private String applicationVersion;

    @Autowired
    private VariableDAO variableDAO;

    @RequestMapping(value = { "/help" }, method = RequestMethod.GET)
    public String getHelp(Model model, HttpServletRequest request) {
        List<Variable> versionList = variableDAO.findByVariableName("version");
        List<Variable> baseDirList = variableDAO.findByVariableName("basedir");
        String version = versionList.size() > 0 ? versionList.get(0).getValue() : "Unknown";
        String baseDir = baseDirList.size() > 0 ? baseDirList.get(0).getValue() : "Unknown";
        if(applicationVersion != null){
            String str[]  = applicationVersion.split("-");
            if(str.length>0){
                applicationVersion = str[0];
            }
        }
        model.addAttribute("serverPath", request.getSession().getServletContext().getRealPath("."));
        model.addAttribute("serverVersion", applicationVersion);
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osVersion", System.getProperty("os.version"));
        model.addAttribute("sqlVersion", version);
        model.addAttribute("sqlBaseDir", baseDir);
        return "admin/help";
    }
}
