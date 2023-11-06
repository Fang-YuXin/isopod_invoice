<?php
header('Content-Type: application/json; charset=UTF-8'); //設定資料類型為 json，編碼 utf-8

	//--------帳號----------//
	if(@$_POST["vid"] != "")
	{
		$uid=$_POST["uid"];
		$vid=$_POST["vid"];		
	}
	else
	{
		echo "請輸入資料";
	}
	//--------內容----------//
	if(@$_POST["vmon"] != "")
	{
		$vmon=$_POST["vmon"];	
		$vyear = $_POST["vyear"];
		//echo $content;
	}
	else
	{
		$vmon=="";
		$vyear="";
	}
	
	//------------讀取資料庫--------------//	

	$servername = "192.168.2.200";
	$username = "fangbib1_123";
	$password = "123";
	$dbname = "fangbib1_testinvoice";
	$conn = new mysqli($servername, $username, $password, $dbname);
	

	//------------------------------------//
	// 建立資料庫連線
	$conn = new mysqli($servername, $username, $password, $dbname);
	// 確認資料是否正常連線
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	}


//<--------------新增資料Start------------>
	$sqlCheckExist = "SELECT * FROM invoice WHERE vid = '$vid' ";
	$resultCheckExist = $conn->query($sqlCheckExist);

	if ($resultCheckExist->num_rows > 0) 
	{
	    echo "已新增過\n";
	} 
	else 
	{
	    // 新增資料到資料庫
	    $sqlInsert = "INSERT INTO `invoice`  VALUES ('".$uid."','".$vid."', '".$vmon."', ".$vyear.", 'not yet','0');";
	    if ($conn->query($sqlInsert) === TRUE) {
	        echo "資料已新增";
	    } else {
	        echo "新增資料時發生錯誤：" . $conn->error;
	    }
	}
//<--------------新增資料End------------>	



$conn->close();
//<-----------------------------End---------------------------->




	//送入時間格式
    $messageArr["status"] = array();
	date_default_timezone_set('America/La_Paz');
	$today = date('Y-m-d\TH:i:sP');//RFC3339格式
	$datetime= array(
	"code" => "0",
	"message" => "Success Insert Message",
	"datetime" => $today
	);	
	$messageArr["status"] = $datetime;	


	if(!empty($_POST['vid']))
	{
		http_response_code(200);
	    //echo json_encode($messageArr);	
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

?>