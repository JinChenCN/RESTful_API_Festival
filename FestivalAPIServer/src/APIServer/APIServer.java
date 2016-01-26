package APIServer;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class APIServer extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of Resource.
        Router router = new Router(getContext());

        // Defines routes
        router.attach("/txt2wave", Text2Wave.class);
        router.attach("/emotionalWave", SayEmotional.class);
        router.attach("/file2wave", File2Wave.class);
        return router;
    }

}
