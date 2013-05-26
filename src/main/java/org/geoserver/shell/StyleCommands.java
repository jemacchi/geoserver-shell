package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;

@Component
public class StyleCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "style list", help = "List style.")
    public String list(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTStyleList styleList = workspace != null ? this.getStyles(workspace) : reader.getStyles();
        List<String> names = styleList.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    private RESTStyleList getStyles(String workspace) throws Exception {
        String url = "/rest/workspaces/" + workspace + "/styles.xml";
        return RESTStyleList.build(HTTPUtils.get(geoserver.getUrl() + url, geoserver.getUser(), geoserver.getPassword()));
    }

    @CliCommand(value = "style get", help = "Get the SLD of a style.")
    public String getSld(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "file", mandatory = false, help = "The output file") File file,
            @CliOption(key = "prettyprint", mandatory = false, unspecifiedDefaultValue = "false", specifiedDefaultValue = "true", help = "Whether to pretty print the SLD or not") boolean prettyPrint
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        String sld;
        if (workspace == null) {
            sld = reader.getSLD(name);
        } else {
            sld = getSLD(name, workspace);
        }
        if (prettyPrint) {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new StringReader(sld));
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            sld = outputter.outputString(document);
        }
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(sld);
            writer.close();
            return file.getAbsolutePath();
        } else {
            return sld;
        }
    }

    private String getSLD(String name, String workspace) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/styles/" + name + ".sld";
        return HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "style delete", help = "Delete a style.")
    public boolean delete(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "purge", mandatory = false, help = "Whether to delete the SLD File from the server or not", unspecifiedDefaultValue = "false") boolean purge
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        if (workspace == null) {
            return publisher.removeStyle(name, purge);
        } else {
            return removeStyle(name, workspace, purge);
        }
    }

    private boolean removeStyle(String name, String workspace, boolean purge) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/styles/" + URLEncoder.encode(name);
        if (purge) {
            url += "?purge=true";
        }
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "style create", help = "Create a style.")
    public boolean create(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "file", mandatory = true, help = "The SLD File") File sldFile
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        if (workspace == null) {
            return publisher.publishStyle(sldFile, name);
        } else {
            return publishStyle(sldFile, name, workspace);
        }
    }

    public boolean publishStyle(File sldFile, String name, String workspace) {
        String sUrl = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/styles";
        if (name != null && !name.isEmpty()) {
            sUrl += "?name=" + URLEncoder.encode(name);
        }
        System.out.println(sUrl);
        String result = HTTPUtils.post(sUrl, sldFile, "application/vnd.ogc.sld+xml", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "style modify", help = "Update a style.")
    public boolean modify(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "file", mandatory = true, help = "The SLD File") File sldFile
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        if (workspace == null) {
            return publisher.updateStyle(sldFile, name);
        } else {
            return updateStyle(sldFile, name, workspace);
        }
    }

    private boolean updateStyle(File sldFile, String name, String workspace) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/styles/" + URLEncoder.encode(name);
        final String result = HTTPUtils.put(url, sldFile, "application/vnd.ogc.sld+xml", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

}
