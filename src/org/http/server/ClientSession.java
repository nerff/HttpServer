package org.http.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientSession implements Runnable {

    private final Socket mSock;
    private List<String> requestHeaders;
    InputStream inputStream = null;

    public ClientSession(Socket clientSock) {
        this.mSock = clientSock;

    }

    @Override
    public void run() {
        readFromClient();
        writeToClient();
    }

    private String extractMethod() {
        return this.requestHeaders.get(0).split("\\s+")[0];
    }

    private void closeConnection() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.mSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* TODO parse http methods in another object manner */
    private void writeToClient() {
        ResponseBuilder responseBuilder = new ResponseBuilder(extractHost(), extractURI(), extractMethod(), mSock);
        responseBuilder.Response();
        closeConnection();
    }

    private String extractURI() {
        return this.requestHeaders.get(0).split("\\s+")[1];
    }

    private String extractHost() {
        for (String var : requestHeaders) {
            if (var.startsWith("Host:")) {
                return var.split("\\s+")[1];
            }
        }
        return "localhost:8888";
    }

    private void readFromClient() {

        try {
            this.requestHeaders = new ArrayList<String>();
            inputStream = mSock.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String header;
                header = reader.readLine();
                if (header.isEmpty() || header == null) {
                    break;
                }
                if (!this.requestHeaders.contains(header)) {
                    this.requestHeaders.add(header);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
