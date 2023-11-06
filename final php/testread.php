<?php
header('Content-Type: application/json; charset=UTF-8'); //設定資料類型為 json，編碼 utf-8
	
//$yearmon = 2023-11;
//抓資料
	if(@$_GET["month"]!="")
	{
		$cmon=$_GET['month'];
		$cyear = $_GET['year'];
		//$number=$_GET['number'];
	}
	else
	{
		$cmon="null";
	}

	if(@$_GET["limit"] != "")
	{
		$limit=$_GET["limit"];	
		//echo $limit;
	}
	else
	{
		$limit="";
	}

/*如果再postman上打yearmon=2023-11是可以分割的
	$yearmon = $_GET['yearmon']; // 從 Query Params 中獲取名為 "yearmon" 的值，這裡假設是 "2023-11"
	$parts = explode("-", $yearmon); // 使用 "-" 作為分隔符將字串分割成陣列
	$year = $parts[0]; // 陣列索引 0 對應到年份
	$month = $parts[1]; // 陣列索引 1 對應到月份

	echo "Year: " . $year . "<br>"; // 輸出年份
	echo "Month: " . $month . "<br>"; // 輸出月份*/

//連結資料庫

	$servername = "192.168.2.200";
	$username = "fangbib1_123";
	$password = "123";
	$dbname = "fangbib1_testinvoice";
	$link = new mysqli($servername, $username, $password, $dbname);
	//$link = mysqli_connect("localhost","root","");
	//mysqli_select_db($link,"root");
	$link = new mysqli($servername, $username, $password, $dbname);

	// 確認資料是否正常連線
	if ($link->connect_error) {
		die("Connection failed: " . $link->connect_error);
	}

	$sql = "SELECT * FROM `checkinvoice`WHERE cmon = ".$cmon." AND cyear = ".$cyear." order by cprice ASC;";
	//echo $sql;
	$result = mysqli_query($link,$sql);
	$messageArr = array();
	$dataarray= array();
	if ($result->num_rows > 0) {
		// output data of each row
		while($row = $result->fetch_assoc()) {
		
			$cid=$row["cid"];
			$cdate=$row["cprice"];			
			$dataarray[] = $row;//將資料一筆一筆丟進dataarray
			
		}
	} 

	//輸出結果	
	$messageArr["data"] = $dataarray;	
	//------------------------------------------------------
	//送入時間格式
    $messageArr["status"] = array();
	date_default_timezone_set('America/La_Paz');
	$today = date('Y-m-d\TH:i:sP');//RFC3339格式
	$datetime= array(
	"code" => "0",
	"message" => "Success",
	"datetime" => $today
	);	
	$messageArr["status"] = $datetime;	


	if(!empty($_GET['month']))
	{
		http_response_code(200);
	    echo json_encode($messageArr);

	}
	else
	{		
	http_response_code(404);	
	$messageArr["data"] =[];//因為沒有帳號，我們就預設讓它為空陣列
	$messageArr["status"] = array();
	date_default_timezone_set('America/La_Paz');
	$today = date('Y-m-d\TH:i:sP');//RFC3339 format
	$datetime= array(
	"code" => "404",
	"message" => "Error Account is null",
	"datetime" => $today
	);	
	$messageArr["status"] = $datetime;
	echo json_encode($messageArr);	
	}




	mysqli_free_result($result);
	mysqli_close($link);

	?>
	    }
	
