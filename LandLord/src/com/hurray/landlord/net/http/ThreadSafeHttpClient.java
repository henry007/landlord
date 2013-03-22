
package com.hurray.landlord.net.http;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class ThreadSafeHttpClient extends DefaultHttpClient {

    public ThreadSafeHttpClient(final HttpParams params) {
        super(params);
    }

    public ThreadSafeHttpClient() {
        super();
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connManager = null;
        HttpParams params = getParams();

        ClientConnectionManagerFactory factory = null;

        // Try first getting the factory directly as an object.
        factory = (ClientConnectionManagerFactory) params
                .getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY);
        if (factory == null) { // then try getting its class name.
            String className = (String) params
                    .getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME);
            if (className != null) {
                try {
                    Class<?> clazz = Class.forName(className);
                    factory = (ClientConnectionManagerFactory) clazz.newInstance();
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException("Invalid class name: " + className);
                } catch (IllegalAccessException ex) {
                    throw new IllegalAccessError(ex.getMessage());
                } catch (InstantiationException ex) {
                    throw new InstantiationError(ex.getMessage());
                }
            }
        }

        if (factory != null) {
            connManager = factory.newInstance(params, registry);
        } else {
            connManager = new ThreadSafeClientConnManager(getParams(), registry);
        }

        return connManager;
    }
}
