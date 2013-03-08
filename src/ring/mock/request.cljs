(ns ring.mock.request
  "Functions to create mock request maps."
  (:import goog.Uri)
  (:require [clojure.string :as string]))

(defn- encode-params
  "Turn a map of parameters into a urlencoded string."
  [params]
  (let [encode #(-> (js/encodeURIComponent %1)
                    (string/replace "%20" "+"))]
    (string/join "&"
                 (for [[k v] params]
                   (str (encode (name k)) "="
                        (encode (str v)))))))

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

(defn- combined-query
  "Create a query string from a URI and a map of parameters."
  [request params]
  (let [query (:query-string request)]
    (if (or query params)
      (string/join "&"
                   (remove string/blank?
                           [query (encode-params params)])))))

(defn- merge-query
  "Merge the supplied parameters into the query string of the request."
  [request params]
  (assoc request :query-string (combined-query request params)))

(defn query-string
  "Set the query string of the request to a string or a map of parameters."
  [request params]
  (if (map? params)
    (assoc request :query-string (encode-params params))
    (assoc request :query-string params)))

(defmulti body
  "Set the body of the request. The supplied body value can be a string or
  a map of parameters to be url-encoded."
  {:arglists '([request body-value])}
  (fn [request x] (type x)))

(defmethod body cljs.core/ObjMap [request params]
  (-> request
      (content-type "application/x-www-form-urlencoded")
      (body (encode-params params))))

(defmethod body nil [request params]
  request)

(defmethod body :default [request content]
  (let [content (str content)]
    (-> request
        (content-length (count content))
        (assoc :body content))))

(defn request
  "Create a minimal valid request map from a HTTP method keyword, a string
  containing a URI, and an optional map of parameters that will be added to
  the query string of the URI. The URI can be relative or absolute. Relative
  URIs are assumed to go to http://localhost."
  ([method uri]
     (request method uri nil))
  ([method uri params]
     (let [uri    (Uri/parse uri)
           host   (if (string/blank? (.getDomain uri)) "localhost" (.getDomain uri))
           port   (if (not= (.getPort uri) -1) (.getPort uri))
           scheme (.getScheme uri)
           path   (.getPath uri)
           query  (if-not (string/blank? (.getQuery uri)) (.getQuery uri))
           request {:server-port    (or port 80)
                    :server-name    host
                    :remote-addr    "localhost"
                    :uri            (if (string/blank? path) "/" path)
                    :query-string   query
                    :scheme         (if (string/blank? scheme)
                                      :http (keyword scheme))
                    :request-method method
                    :headers        {"host" (if port
                                              (str host ":" port)
                                              host)}}]
       (if (#{:get :head} method)
         (merge-query request params)
         (body request params)))))
