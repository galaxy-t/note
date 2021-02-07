

# 使用 Docker 拉起 MinIO 

    
    docker run -d -p 9000:9000 --name minio -e "MINIO_ACCESS_KEY=admin" -e "MINIO_SECRET_KEY=admin" -v /usr/local/minio/data:/data minio/minio server /data
    
    docker run -itd -p 9000:9000    映射端口 9000到宿主机9000    
    --name minio   容器名称为 minio
    -e "MINIO_ACCESS_KEY=admin"   -e "MINIO_SECRET_KEY=admin"   帐号密码设置   
    -v /usr/local/minio/data:/data   -v /usr/local/minio/conf:/root/.minio            将 数据目录和配置目录映射到宿主机
    minio/minio server /data        镜像名称 minio/minio  
    