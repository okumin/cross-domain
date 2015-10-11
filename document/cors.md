# CORS

## CORS について

CORS(Cross-Origin Resource Sharing)は W3C が提案する、クロスサイト HTTP リクエストを実現するための仕様。  
専用の HTTP ヘッダを用いて、安全にドメインをまたいだリクエストを可能とする。  
XMLHttpRequest Level2 が実装されているブラウザ上で動作する。  

仕様の詳細は [HTTP access control (CORS)](https://developer.mozilla.org/ja/docs/HTTP_access_control) に詳しい。

## 例

### シンプルなリクエスト(GET, POST)

CORS に対応した Json API を「other.mofu.poyo」に用意する。  
この API は Origin ヘッダが「 http://foo.mofu.poyo 」の場合にクロスリクエストを許可する。  

GET API と POST API を呼び出すページを用意する。

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="getUser()" value="GET">
    <input type="button" onclick="postUser()" value="POST">
    <script>
        function request(method, url, send) {
            var xhr = new XMLHttpRequest();
            xhr.open(method, url);
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        var user = JSON.parse(xhr.responseText);
                        console.log(user);
                        document.getElementById("user_id").innerText = user.id;
                        document.getElementById("user_name").innerText = user.name;
                    } catch (e) {
                        console.log(e);
                        alert(e.message);
                    }
                }
            };
            xhr.onerror = function (e) {
                console.error(e);
                console.error(xhr);
                alert("Error!");
            };
            send(xhr);
        }

        function getUser() {
            request("GET", "http://other.mofu.poyo/cors_users/334", function (xhr) {
                xhr.send(null);
            });
        }
        function postUser() {
            request("POST", "http://other.mofu.poyo/cors_users", function (xhr) {
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.send("name=shinzan");
            });
        }
    </script>
</body>
```

[http://foo.mofu.poyo/cors/call_api_simple](http://foo.mofu.poyo/cors/call_api_simple) にアクセスし、ボタンを押すとリクエストに成功する。  
[シンプルなリクエスト](https://developer.mozilla.org/ja/docs/HTTP_access_control#Simple_requests) の条件を満たしているため、直接リクエストがサーバに送られる。  

以下はサーバ側のログ。

```
[info] - application - GET /cors_users/334
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_simple]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
[info] - application - POST /cors_users
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Content-Length -> [12]
[info] - application - header: Content-Type -> [application/x-www-form-urlencoded]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_simple]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
```

### プリフライトリクエスト(PUT, POST with カスタムヘッダ)

CORS に対応した Json API を「other.mofu.poyo」に用意する。  
この API は Origin ヘッダが「 http://foo.mofu.poyo 」の場合にクロスリクエストを許可する。  

以下のページを [http://foo.mofu.poyo/cors/call_api_preflight](http://foo.mofu.poyo/cors/call_api_preflight) に用意する。

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="putUser()" value="PUT">
    <input type="button" onclick="postUser()" value="POST">
    <script>
        function request(method, url, send) {
            var xhr = new XMLHttpRequest();
            xhr.open(method, url);
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        var user = JSON.parse(xhr.responseText);
                        console.log(user);
                        document.getElementById("user_id").innerText = user.id;
                        document.getElementById("user_name").innerText = user.name;
                    } catch (e) {
                        console.log(e);
                        alert(e.message);
                    }
                }
            };
            xhr.onerror = function (e) {
                console.error(e);
                console.error(xhr);
                alert("Error!");
            };
            send(xhr);
        }

        function putUser() {
            request("PUT", "http://other.mofu.poyo/cors_users/1000", function (xhr) {
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.send("name=mofutan");
            });
        }
        function postUser() {
            request("POST", "http://other.mofu.poyo/cors_users", function (xhr) {
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.send("name=shinzan");
            });
        }
    </script>
</body>
```

PUT リクエスト及びカスタムヘッダ付き POST リクエストは [プリフライトリクエスト](https://developer.mozilla.org/ja/docs/HTTP_access_control#Preflighted_requests) の対象となる。  
したがって、それぞれのリクエスト前に OPTIONS リクエストが呼び出される。  

PUT リクエストの際のログ。

```
[info] - application - OPTIONS /cors_users/1000
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Access-Control-Request-Headers -> [content-type]
[info] - application - header: Access-Control-Request-Method -> [PUT]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_preflight]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
[info] - application - PUT /cors_users/1000
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Content-Length -> [12]
[info] - application - header: Content-Type -> [application/x-www-form-urlencoded]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_preflight]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
```

POST リクエストの際のログ。

```
[info] - application - OPTIONS /cors_users
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Access-Control-Request-Headers -> [content-type, x-requested-with]
[info] - application - header: Access-Control-Request-Method -> [POST]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_preflight]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
[info] - application - POST /cors_users
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Content-Length -> [12]
[info] - application - header: Content-Type -> [application/x-www-form-urlencoded]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_preflight]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
[info] - application - header: X-Requested-With -> [XMLHttpRequest]
```

### クレデンシャルを含むリクエスト

CORS に対応した Json API を「other.mofu.poyo」に用意する。  
この API は Origin ヘッダが「http://foo.mofu.poyo」の場合にクロスリクエストを許可する。  
またこの API は [Access-Control-Allow-Credentials](https://developer.mozilla.org/ja/docs/HTTP_access_control#Access-Control-Allow-Credentials) ヘッダを true で返す。  

以下のページを [http://foo.mofu.poyo/cors/call_api_credentials](http://foo.mofu.poyo/cors/call_api_credentials) に用意する。  
またこのページを開くと、Cookie に「session=1a1a1a」がセットされるようにしておく。

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="getUser()" value="GET">
    <input type="button" onclick="getUserWithCredentials()" value="GET with credentials">
    <script>
        function request(method, url, send) {
            var xhr = new XMLHttpRequest();
            xhr.open(method, url);
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        var user = JSON.parse(xhr.responseText);
                        console.log(user);
                        document.getElementById("user_id").innerText = user.id;
                        document.getElementById("user_name").innerText = user.name;
                    } catch (e) {
                        console.log(e);
                        alert(e.message);
                    }
                }
            };
            xhr.onerror = function (e) {
                console.error(e);
                console.error(xhr);
                alert("Error!");
            };
            send(xhr);
        }

        function getUser() {
            request("GET", "http://other.mofu.poyo/cors_users/334", function (xhr) {
                xhr.send(null);
            });
        }
        function getUserWithCredentials() {
            request("GET", "http://other.mofu.poyo/cors_users/334", function (xhr) {
                xhr.withCredentials = true;
                xhr.send(null);
            });
        }
    </script>
</body>
```

「GET」ボタンと「GET with credentials」ボタンの違いは、`XMLHttpRequest` オブジェクトの `withCredentials` プロパティを `true` にセットしているか否かである。  

「GET」ボタンを押したときは Cookie が送信されない。

```
[info] - application - GET /cors_users/334
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_credentials]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
```

「GET with credentials」ボタンを押すと Cookie が送信される。

```
[info] - application - GET /cors_users/334
[info] - application - header: Accept -> [*/*]
[info] - application - header: Accept-Encoding -> [gzip, deflate, sdch]
[info] - application - header: Accept-Language -> [ja,en-US;q=0.8,en;q=0.6]
[info] - application - header: Connection -> [keep-alive]
[info] - application - header: Cookie -> [session=1a1a1a]
[info] - application - header: Host -> [other.mofu.poyo]
[info] - application - header: Origin -> [http://foo.mofu.poyo]
[info] - application - header: Referer -> [http://foo.mofu.poyo/cors/call_api_credentials]
[info] - application - header: User-Agent -> [Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36]
```

## 参考文献

* [HTTP access control (CORS)](https://developer.mozilla.org/ja/docs/HTTP_access_control)
* [Cross-Origin Resource Sharing](http://www.w3.org/TR/cors/)
