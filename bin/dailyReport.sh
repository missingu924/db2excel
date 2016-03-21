#/bin/sh

#执行登陆用户根目录下的环境变量设置文件，需要各现场根据实际情况调整
. /export/home/nwom/.profile

cd $HOME/db2excel/bin

./db2excel.sh dailyReport.xml
