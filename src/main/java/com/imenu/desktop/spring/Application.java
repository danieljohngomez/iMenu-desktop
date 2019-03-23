package com.imenu.desktop.spring;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main( String[] args ) throws IOException {
        SpringApplication.run( Application.class, args );
        openBrowser();
    }

    public static void openBrowser()
        {
            String url = "http://localhost:8080/menu";
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            try{

                if ( os.contains( "win" ) ) {

                    // this doesn't support showing urls in the form of "page.html#nameLink"
                    rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);

                } else if ( os.contains( "mac" ) ) {

                    rt.exec( "open " + url);

                } else if ( os.contains( "nix" ) || os.contains( "nux" ) ) {

                    // Do a best guess on unix until we get a platform independent way
                    // Build a list of browsers to try, in this order.
                    String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                            "netscape","opera","links","lynx"};

                    // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                    StringBuffer cmd = new StringBuffer();
                    for (int i=0; i<browsers.length; i++)
                        cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

                    rt.exec(new String[] { "sh", "-c", cmd.toString() });

                } else {
                    return;
                }
            }catch (Exception e){
                return;
            }
            return;
        }

    }
