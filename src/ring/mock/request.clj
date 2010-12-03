(ns ring.mock.request
  "Functions to create mock request maps."
  (:require [clojure.string :as string])
  (:use [hiccup.page-helpers :only (encode-params)])
  (:import java.util.Map
           java.io.ByteArrayInputStream
           [java.net URI URLEncoder]))

(defn- query-string
  "Create a query string from a URI and a map of parameters."
  [uri params]
  (let [query (.getRawQuery uri)]
    (if (or query params)
      (string/join "&"
        (remove string/blank?
                [query (encode-params params)])))))

(defn request
  "Create a minimal valid request map from a HTTP method keyword and
  a URI string. The URI can be relative or absolute."
  ([method uri]
     (request method uri nil))
  ([method uri params]
     (let [uri    (URI. uri)
           port   (.getPort uri)
           scheme (.getScheme uri)
           path   (.getRawPath uri)]
       {:server-port    (if (= port -1) 80 port)
        :server-name    (or (.getHost uri) "localhost")
        :remote-addr    "localhost"
        :uri            (if (string/blank? path) "/" path)
        :query-string   (query-string uri params)
        :scheme         (or (keyword scheme) :http)
        :request-method method
        :headers        {}})))

(defn header
  "Add a header to a request."
  [request name value]
  (assoc-in request [:headers name] value))

(defprotocol Streamable
  (to-stream [x] "Turn x into an InputStream"))

(extend-protocol Streamable
  String
  (to-stream [s]
    (ByteArrayInputStream. (.getBytes s)))
  Map
  (to-stream [m]
    (to-stream (encode-params m))))

(defn body
  "Set the body of the request. The supplied body value can be a string or
  a map of parameters to be url-encoded."
  [request body]
  (assoc request :body (to-stream body)))
