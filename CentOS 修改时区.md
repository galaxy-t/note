## CentOS 修改时区
    
    最新版本的 CentOS

1. 输入 tzselect, 依次选择 5, 9, 1, 最后再进行确认, 这样能够把北京时区下载下来
2. 然后输入 rm /etc/localtime 删除原来的时区链接
3. 使用 ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime 重新链接正确的时区
4. 输入 date 看一下会发现已经修改成了北京时间
