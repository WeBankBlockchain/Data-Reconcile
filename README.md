# 组件介绍
传统企业间的对账依赖于对账双⽅的中心化账本。中心化账本在对账期间如果出现账不平的情况，排查会⾮常耗时耗力。区块链作为信任的机器，具有不可篡改、分布式账本等特性，基于区块链的对账能够在对账不一致的情况下，找到⼀个可信的客观依据，从而减少因对账不平造成的排查成本。
WeBankBlockchain-Data-Reconcile是一款基于区块链的对账组件，提供基于区块链智能合约账本的通用化数据对账解决方案，并提供了一套可动态扩展的对账框架，支持定制化开发。

## 关键特性
- 支持自定义对账数据
- 支持自定义对账规则
- 支持多个对账任务, 提供对账任务配置
- 支持多种对账文件格式，可扩展
- 支持自定义对账任务和方式，手动或自动、任务频次、时间可配置
- 支持多种对账文件托管方式，如文件管理系统或服务器，可扩展

## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件   | 说明                                                         | 备注 |
| ---------- | ------------------------------------------------------------ | ---- |
| FISCO-BCOS | \>= 2.0， 1.x版本请参考V0.5版本 dev分支                      |      |
| Bash       | 需支持Bash（理论上来说支持所有ksh、zsh等其他unix shell，但未测试） |      |
| Java       | \>= JDK[1.8]                                                 |      |
| Git        | 下载的安装包使用Git                                          |      |
| MySQL      | \>= mysql-community-server[5.7]                              |      |
| FTP        | 需要时安装                                                             |      |


## 文档
- [**中文**](https://data-doc.readthedocs.io/zh_CN/dev/docs/WeBankBlockchain-Data-Reconcile/index.html)
- [**快速安装**](https://data-doc.readthedocs.io/zh_CN/dev/docs/WeBankBlockchain-Data-Reconcile/install.html)


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目左上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/Data-Reconcile/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。


## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。

