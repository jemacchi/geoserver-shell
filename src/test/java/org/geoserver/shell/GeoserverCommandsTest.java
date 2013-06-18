package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class GeoserverCommandsTest extends BaseTest {

    @Test
    public void set() {
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        String url = "http://localhost:8080/geoserver";
        String user = "admin";
        String pass = "geoserver";
        commands.set(url, user, pass);
        assertEquals(url, gs.getUrl());
        assertEquals(user, gs.getUser());
        assertEquals(pass, gs.getPassword());
    }

    @Test
    public void show() {
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        String url = "http://localhost:8080/geoserver";
        String user = "admin";
        String pass = "geoserver";
        commands.set(url, user, pass);
        String expected = "http://localhost:8080/geoserver admin geoserver";
        String actual = commands.show();
        assertEquals(expected, actual);
    }

    @Test
    public void reset() throws Exception {
        String url = "/geoserver/rest/reset";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.reset();
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void reload() throws Exception {
        String url = "/geoserver/rest/reload";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.reload();
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }
}