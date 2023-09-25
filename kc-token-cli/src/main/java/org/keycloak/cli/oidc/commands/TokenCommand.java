package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.Output;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import picocli.CommandLine;

@CommandLine.Command(name = "token")
public class TokenCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextName;
    @CommandLine.Option(names = {"--type"}, description = "Token type to return", defaultValue = "access")
    String tokenType;
    @CommandLine.Option(names = {"--decode"}, description = "Decode token", defaultValue = "false")
    boolean decode;
    @CommandLine.Option(names = {"--offline"}, description = "Offline mode", defaultValue = "false")
    boolean offline;

    @Override
    public void run() {
        try {
            String token = getToken();
            if (decode) {
                String decoded = TokenParser.parse(token).decoded();
                Output.println(decoded);
            } else {
                Output.println(token);
            }
        } catch (Exception e) {
            Error.onError(e);
        }
    }

    public String getToken() throws OpenIDException, ConfigException {
        ConfigHandler configHandler = ConfigHandler.get();
        Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();
        OpenIDClient openIDClient = OpenIDClient.create(configHandler, context);
        return openIDClient.getToken(tokenType, offline);
    }

}
