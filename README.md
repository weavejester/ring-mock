# Ring-Mock

Ring-Mock is a library for creating [Ring][1] request maps for testing
purposes.

[1]: https://github.com/mmcgrana/ring

## Example

    (use 'ring.mock.request)
    
    (your-handler (request :get "/doc/10"))

    (your-handler (-> (request :post "/doc")
                      (body {:title "foo"})))

## Installation

Add the following to your Leiningen dependencies:

    [ring-mock "0.1.0"]
