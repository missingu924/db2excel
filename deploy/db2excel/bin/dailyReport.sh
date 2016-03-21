#/bin/sh

#运行该系统账户需要设置的环境变量，需要根据各项目实际情况修改
. /export/home/nwom/.profile

cd $HOME/db2excel/bin

./db2excel.sh dailyReport.xml
