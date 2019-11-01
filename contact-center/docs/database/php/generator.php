<?php
/**
 * 生成mysql数据字典
 */
header("Content-type: text/html; charset=utf-8");

// 配置数据库
// options
$shortopts  = "";
$shortopts .= "H:";  // host
$shortopts .= "P:"; // port
$shortopts .= "d:"; // database
$shortopts .= "u:"; // username
$shortopts .= "p:"; // password
$shortopts .= "v:"; // version
$options = getopt($shortopts);

define('DB_HOST',$options["H"]);
define('DB_PORT', (int)$options["P"]);
define('DB_NAME',$options["d"]);
define('DB_USER',$options["u"]);
define('DB_PASS',$options["p"]);
define('DB_CHAR', "utf8");

// 其他配置
$title = '春松客服-数据字典';
$version = $options["v"];

$conn = @mysqli_connect(DB_HOST.':'.DB_PORT,DB_USER,DB_PASS) or die("Mysql connect is error.");
mysqli_select_db($conn, DB_NAME);
mysqli_query($conn, 'SET NAMES '.DB_CHAR);
$table_result = mysqli_query($conn, 'show tables');
// 取得所有的表名
while($row = mysqli_fetch_array($table_result)) {
	$tables [] ['TABLE_NAME'] = $row [0];
}

// 循环取得所有表的备注及表中列消息
foreach($tables as $k => $v){
	$sql = 'SELECT * FROM ';
	$sql .= 'INFORMATION_SCHEMA.TABLES ';
	$sql .= 'WHERE ';
	$sql .= "table_name = '{$v['TABLE_NAME']}'  AND table_schema = '".DB_NAME."'";
	$table_result = mysqli_query($conn, $sql);
	while($t = mysqli_fetch_array($table_result)) {
		$tables [$k] ['TABLE_COMMENT'] = $t ['TABLE_COMMENT'];
	}
	
	$sql = 'SELECT * FROM ';
	$sql .= 'INFORMATION_SCHEMA.COLUMNS ';
	$sql .= 'WHERE ';
	$sql .= "table_name = '{$v['TABLE_NAME']}' AND table_schema = '".DB_NAME."'";
	
	$fields = array();
	$field_result = mysqli_query($conn, $sql);
	while($t = mysqli_fetch_array($field_result)) {
		$fields [] = $t;
	}
	$tables [$k] ['COLUMN'] = $fields;
}
mysqli_close($conn);

$content = '';
// 循环所有表
foreach($tables as $k => $v){
	$content .= '<p><h2>'. $v['TABLE_COMMENT'] . '&nbsp;</h2>';
	$content .= '<table  border="1" cellspacing="0" cellpadding="0" align="center">';
	$content .= '<caption>' . $v ['TABLE_NAME'] . '  ' . $v ['TABLE_COMMENT'] . '</caption>';
	$content .= '<tbody><tr><th>字段名</th><th>数据类型</th><th>默认值</th>
    <th>允许非空</th>
    <th>自动递增</th><th>备注</th></tr>';
	$content .= '';
	
	foreach($v ['COLUMN'] as $f){
		$content .= '<tr><td class="c1">' . $f ['COLUMN_NAME'] . '</td>';
		$content .= '<td class="c2">' . $f ['COLUMN_TYPE'] . '</td>';
		$content .= '<td class="c3">&nbsp;' . $f ['COLUMN_DEFAULT'] . '</td>';
		$content .= '<td class="c4">&nbsp;' . $f ['IS_NULLABLE'] . '</td>';
		$content .= '<td class="c5">' . ($f ['EXTRA'] == 'auto_increment' ? '是' : '&nbsp;') . '</td>';
		$content .= '<td class="c6">&nbsp;' . $f ['COLUMN_COMMENT'] . '</td>';
		$content .= '</tr>';
	}
	$content .= '</tbody></table></p>';
}

// 输出
$date = date('Y-m-d');
$html = <<<EOT
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>$title</title>
<style>
body,td,th {font-family:"宋体"; font-size:12px;}
table{border-collapse:collapse;border:1px solid #CCC;background:#6089D4;}
table caption{text-align:left; background-color:#fff; line-height:2em; font-size:14px; font-weight:bold; }
table th{text-align:left; font-weight:bold;height:26px; line-height:25px; font-size:16px; border:3px solid #fff; color:#ffffff; padding:5px;}
table td{height:25px; font-size:12px; border:3px solid #fff; background-color:#f0f0f0; padding:5px;}
.c1{ width: 150px;}
.c2{ width: 130px;}
.c3{ width: 70px;}
.c4{ width: 80px;}
.c5{ width: 80px;}
.c6{ width: 300px;}
</style>
</head>
<body>
<h1 style="text-align:center;">$title<span style="font-size:14px;color: #ccc;margin-left:20px;">(生成日期: $date)</span></h1>
<p>版本：$version</p>
$content
</body>
</html>
EOT;
file_put_contents('index.html', $html);
echo 'success!';
?>