# JSONP

## JSONP について

JSONP(JSON with Padding)は非同一生成元上の API を呼び出す手法の一種。  
コールバック関数を実行する JavaScript を、script タグにより呼び出すことで実現する。  
同一生成元ポリシーは script タグによる埋め込みには適用されないため、この手法が成立する。

## 例

### JSONP による API アクセス

ユーザー情報を取得する API を用意する。  
この API は、クエリパラメータ「callback」により指定された関数に、ユーザー情報である Json を適用するスクリプトを返す。  
例えば、[http://other.mofu.poyo/users/334?callback=load](http://other.mofu.poyo/users/334?callback=load) は以下のスクリプトを返す。

```javascript
load({"id":334,"name":"okumin"})
```

ユーザー情報を取得して表示するページを [http://foo.mofu.poyo/jsonp/call_api](http://foo.mofu.poyo/jsonp/call_api) に設置する。  

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="call()" value="Click">
    <script>
        function call() {
            var script = document.createElement("script");
            script.setAttribute("type", "text/javascript");
            script.setAttribute("src", "http://other.mofu.poyo/users/334?callback=show");
            document.body.appendChild(script);
        }

        function show(user) {
            console.log(user);
            document.getElementById("user_id").innerText = user.id;
            document.getElementById("user_name").innerText = user.name;
        }
    </script>
</body>
```

ボタンを押すと、[http://other.mofu.poyo/users/334?callback=show](http://other.mofu.poyo/users/334?callback=show) を呼び出す script タグを生成する。  
API が呼び出されると、`callback` に指定した `show` 関数が実行される。  
`show` 関数はユーザー情報(JSON)を受け取り、画面に表示する。  
以上の手順により、「foo.mofu.poyo」から「other.mofu.poyo」の API にアクセスすることができる。

## 参考文献

* [JSONP](https://ja.wikipedia.org/wiki/JSONP)
