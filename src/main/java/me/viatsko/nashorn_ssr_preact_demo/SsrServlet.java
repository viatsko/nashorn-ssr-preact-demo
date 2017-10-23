package me.viatsko.nashorn_ssr_preact_demo;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SsrServlet", value = "/ssr")
public class SsrServlet extends HttpServlet {
  private static ScheduledExecutorService globalScheduledThreadPool = Executors.newScheduledThreadPool(20);

  @Override
  public void init() throws ServletException {
    super.init();

    ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine();

    scriptEngine.getContext().setAttribute("__NASHORN_POLYFILL_TIMER__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);

    String polyfill = "var global = this;\n"
            + "window = global;\n"
            + "global.console = {};\n"
            + "console.debug = print;\n"
            + "console.warn = print;\n"
            + "console.log = print;";

    try {
      scriptEngine.eval(polyfill);
      scriptEngine.eval(CharStreams.toString(new InputStreamReader(this.getServletContext().getResourceAsStream("/bundles/preact.bundle.js"), Charsets.UTF_8)));
    } catch (ScriptException | IOException e) {
      throw new IllegalStateException("Failed to eval script!", e);
    }

    this.getServletContext().setAttribute("se", scriptEngine);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Properties properties = System.getProperties();

    response.setContentType("text/html");

    try {
      Object markup = ((Invocable) this.getServletContext().getAttribute("se")).invokeFunction("renderOnServer");
      response.getWriter().write((String) markup);
    } catch (ScriptException | NoSuchMethodException e) {
      throw new IllegalStateException("Failed to eval script!", e);
    }
  }
}
