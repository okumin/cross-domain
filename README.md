# cross-domain

クロスドメインなアクセスを試してみた & メモを残しておく。  

HOSTS ファイル

```
127.0.0.1	foo.mofu.poyo other.mofu.poyo mofu.poyo
```

起動

```
sudo sbt -Dhttp.port=80 run
```

* [同一生成元ポリシー(Same-origin policy)](https://github.com/okumin/cross-domain/blob/master/document/same_origin_policy.md)
* [document.domain](https://github.com/okumin/cross-domain/blob/master/document/document_domain.md)
* [window.postMessage](https://github.com/okumin/cross-domain/blob/master/document/post_message.md)
* [JSONP](https://github.com/okumin/cross-domain/blob/master/document/jsonp.md)
* [CORS](https://github.com/okumin/cross-domain/blob/master/document/cors.md)
