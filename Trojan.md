### 下载

    官方地址: https://trojan-gfw.github.io/
             https://github.com/trojan-gfw/trojan

### 下载程序, 解压, 并移动到指定目录

1. `wget https://github.com/trojan-gfw/trojan/releases/download/v1.16.0/trojan-1.16.0-linux-amd64.tar.xz`
2. `tar -xvf trojan-1.16.0-linux-amd64.tar.xz`
3. `mv trojan /usr/local/trojan`

### 安装 certbot 并为域名获取一个 SSL 证书

1. `准备一个域名, 然后将域名解析到你要安装 trojan 的服务器`
2. `yum install -y certbot` 安装 `certbot`
3. `certbot certonly --standalone -d xxxxxx.com` 这句话的意思是启动 certbot 工具, certonly 的意思是只申请证书,
   --standalone 选项告诉 Certbot 使用 独立模式（Standalone Mode）进行验证，而不是使用现有的 Web 服务器（例如 Nginx 或
   Apache）进行 HTTP 验证. Certbot 会在申请证书时临时启动一个内置的 Web 服务器，使用该服务器来响应 Let's Encrypt 的域名验证请求.
4. 首次执行步骤 3 命令的时候会让你填一个邮箱, 这是必填的, 随便填一个即可, 后面的各种询问直接输入 Y 即可, 然后回车.
5. 成功会提示: `Successfully received certificate.`
6. 在 `/etc/letsencrypt/live/xxxxxx.com/` 目录下就是申请到的证书了,
7. 步骤 5 会提示各种证书的路径以及过期时间, 到期(三个月过期)会自动续签

### 启动 trojan

1. `cd /usr/local/trojan` 进入 `trojan` 安装目录
2. `vim config.json` 编辑配置文件, `password` 默认有两个删除,
   如果不需要两个删除掉一个自己修改一下另一个, `ssl下的cert和key` 设置为刚才申请的证书的绝对路径, cert 是 fullchain.pem
   文件, key 是 privkey.pem 文件, 然后保存退出
3. `nohup ./trojan -c config.json  > trojan.log 2>&1 &` 使用该命令后台运行 trojan, 日志打印到当前目录下的 trojan.log
4. `tail -1000f trojan.log` 查看日志, 会打印出 Welcome to trojan 1.16.0 [2024-12-31 14:41:32] [WARN] trojan service (
   server) started at 0.0.0.0:443 这些文字

### 在 `Shadowrocket` 中使用

1. 右上角点加号
2. 类型选择 `Trojan`
3. 地址写域名, 端口 443, 密码填设置的密码
4. 其它的默认
5. 测试一下速度
