(ns ring.mock.request
  "Functions to create mock request maps."
  (:require [clojure.string :as string])
  (:import java.util.Map
           java.io.ByteArrayInputStream
           [java.net URI URLEncoder]))

(defn- encode-params
  "Turn a map of parameters into a urlencoded string."
  [params]
  (string/join "&"
               (for [[k v] params]
                 (str (URLEncoder/encode (name k)) "="
                      (URLEncoder/encode (str v))))))

(defn- append-query
  "Encode and append a map of parameters to a url query string."
  [query params]
  (if (or query params)
    (string/join "&"
                 (remove string/blank?
                         [query (encode-params params)]))))

(defn header
  "Add a HTTP header to the request map."
  [request header value]
  (let [header (string/lower-case (name header))]
    (assoc-in request [:headers header] (str value))))

(defn content-type
  "Set the content type of the request map."
  [request mime-type]
  (-> request
      (assoc :content-type mime-type)
      (header :content-type mime-type)))

(defn content-length
  "Set the content length of the request map."
  [request length]
  (-> request
      (assoc :content-length length)
      (header :content-length length)))

(defn query-string
  "Add a map of parameters to the query string of the request."
  [request params]
  (update-in request [:query-string] #(append-query % params)))

(defprotocol BodyEncodable
  "Types which can attach themselves to a request as a body"
  (encode-body-to [this request] "Attach `this` to request as the request body"))

(defn body
  "Set the body of the request. The supplied body value can be a string or
  a map of parameters to be url-encoded."
  [request body]
  (encode-body-to body request))

(extend-protocol BodyEncodable
  (class (byte-array 0))
  (encode-body-to [byte-array request]
    (-> request
        (content-length (count byte-array))
        (assoc :body (ByteArrayInputStream. byte-array))))
  String
  (encode-body-to [string request] (encode-body-to (.getBytes string) request))
  Map
  (encode-body-to [map request]
    (encode-body-to (encode-params map)
                    (content-type request "application/x-www-form-urlencoded"))))

(defn request
  "Create a minimal valid request map from a HTTP method keyword, a string
  containing a URI, and an optional map of parameters that will be added to
  the query string of the URI. The URI can be relative or absolute. Relative
  URIs are assumed to go to http://localhost."
  ([method uri]
     (request method uri nil))
  ([method uri params]
     (let [uri    (URI. uri)
           host   (or (.getHost uri) "localhost")
           port   (if (not= (.getPort uri) -1) (.getPort uri))
           scheme (.getScheme uri)
           path   (.getRawPath uri)
           query  (.getRawQuery uri)
           req    {:server-port    (or port 80)
                   :server-name    host
                   :remote-addr    "localhost"
                   :uri            (if (string/blank? path) "/" path)
                   :query-string   query
                   :scheme         (or (keyword scheme) :http)
                   :request-method method
                   :headers        {"host" (if port
                                             (str host ":" port)
                                             host)}}]
       (if params
         (case method
           :get  (query-string req params)
           :post (body req params)
           req)
         req))))
