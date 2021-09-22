# GoLand

    以下介绍使用 GoLand 如何创建及开发一个项目， 项目名： mall

## 基础概念

### GOPATH 用于放置和管理一些应用软件，GOPATH 下会自动生成以下三个文件夹
    
    GOPATH 可以配置 Global GOPATH(全局 GOPATH） 和 Project GOPATH（项目 GOPATH）两个
    Global GOPATH：用于全局使用，类似于 maven 的 本地 repository
    Project GOPATH：项目单独配置的依赖库，类似于 java 项目将 jar 包放入项目中的 lib 一样，只不过可以多个项目共用该文件夹
    使用 Project GOPATH 会自动忽略 Global GOPATH 的配置，建议也是这么做的
    但是个人建议使用 Global GOPATH 比较好

1. bin：用于管理 go 官方的一些应用软件（如：goget，goimport等），这些软件与代码开发无关，一般为代码管理工具等，大概类似于 go 官方提供的 maven，sonar 等这一类东西
2. src：用于放置 go 官方的一些 sdk（之前一些老版本会使用到这个目录），其实就是一些项目的源代码
3. pkg：用于放置一些第三方的 sdk ，也是一些项目的源代码，只不过再 go.mod 出现之后下载的东西都放到了这个文件夹下

### go.mod 用于管理 go 项目中依赖的一些 sdk，类似于 maven 的 pom.xml
    
    go.mod 管理的一些依赖会下载到 GOPATH 中的 pkg 中
    一个 go.mod 文件就代表了一个项目，它放置再项目的根目录下，其同目录的所有的文件夹（包含它们下面的文件夹）会被认为是不同的包

#### 命令

1. go mod tidy 拉取 go.mod 中的依赖

## 创建

    File -> New -> Project 左侧选择 Go
    Location: 项目路径，如：D:\dev\go\mall
    GOROOT：本地 Go 安装版本
    Environment：项目环境变量设置，一般只需要设置 GOPROXY 即可
        GOPROXY：代理设置，一般设置未国内的代理（https://goproxy.cn）

## 结构

### 默认结构

    mall（根目录）
        go.mod 

### 开发结构

    mall（根目录）
        product(产品包）
        user(用户包)
        main.go
        go.mod

    一个项目可以有多个 main 函数，但是一般在项目的根目录会创建一个 main.go 的文件，里面的 main 方法用于作为启动函数，类似于 springboot 的启动类一样



    
