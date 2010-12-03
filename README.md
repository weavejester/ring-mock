# Ring-Mock

Ring-Mock is a library for creating [Ring][1] request maps for testing
purposes.

[1]: https://github.com/mmcgrana/ring

## Documentation

* [API Documentation](http://weavejester.github.com/ring-mock)

## Example

    (ns your-app.test.core
      (:use your-app.core
            clojure.test
            ring.mock.request))
       
    (deftest your-handler-test
      (is (= (your-handler (request :get "/doc/10"))
             {:status 200
              :headers {"content-type" "text/plain"}
              :body "Your expected result"})))

## Installation

Add the following to your Leiningen dependencies:

    [ring-mock "0.1.0"]
