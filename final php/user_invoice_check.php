<?php
header('Content-Type: application/json; charset=UTF-8'); //設定資料類型為 json，編碼 utf-8

	//--------帳號----------//
	if(@$_GET["uid"] != "")
	{
	
		$uid=$_GET["uid"];
	}
	else
	{
		echo "查無user";
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




	//查特定使用者發票資料可以兌獎or expired 的發票號碼&資訊
	
    $sqlselect = "SELECT DISTINCT `vid`,`vmon`,`vyear`,`statue`,`price`,`cdate` FROM invoice INNER JOIN checkinvoice ON invoice.vmon=checkinvoice.cmon AND invoice.vyear=checkinvoice.cyear WHERE uid ='".$uid."';";
	$result = $conn->query($sqlselect);
	//echo $sqlselect;
	//echo ok



	if ($result->num_rows > 0) 
	{
	    // output data of each row
	    while ($row = $result->fetch_assoc()) 
	    {
	    	$vid_value=$row['vid'];
	    	$cdate_value=$row['cdate'];
	    	//<-----------------------判斷過期------------------->
			/*if (strtotime($cdate) < strtotime('today'))
			{
    			//echo $vid."已過期";
				//echo ok
    			$sqlupdate = "UPDATE `invoice` SET `statue` = 'expired' ,`price` = '0' WHERE `vid` = '".$vid."' AND `uid` ='".$uid."';";
    			$resultupdate = $conn->query($sqlupdate);
    			
	
			}*/
			
	        $dataarray[] = $row;
	    }
	}
	/*echo 'dataarray\n';
echo json_encode($dataarray);

	$today = CUR_DATE();
	echo $today;
	$expired="expired";
	$sqlagain="SELECT DISTINCT `vid`,`vmon`,`vyear`,`statue`,`price`,`cdate` FROM invoice INNER JOIN checkinvoice ON invoice.vmon=checkinvoice.cmon AND invoice.vyear=checkinvoice.cyear AND checkinvoice.cdate > $today WHERE uid ='".$uid."';";
	echo $sqlagain;
	$result = $conn->query($sqlselect);
	if ($result->num_rows > 0) 
	{
	    // output data of each row
	    while ($row = $result->fetch_assoc()) 
	    {
	        $dataarray_again[] = $row;
	    }
	}
	echo 'dataarray_again\n';
echo json_encode($dataarray_again);*/



//把發票號碼&年月份&過期日記得，方便下面的兌獎判斷
	$vid=array();
	$vmon=array();
	$vyear=array();
	$cdate=array();

	foreach ($dataarray as $item) 
	{
		array_push($vid,$item['vid']);
		array_push($vmon,$item['vmon']);
		array_push($vyear,$item['vyear']);
		array_push($cdate,$item['cdate']);
	}

	//echo '<pre>'; print_r($cdate_check); echo '</pre>';
	/*echo '<pre>'; print_r($vid); echo '</pre>';
	echo '<pre>'; print_r($cdate); echo '</pre>';*/
	//echo '<pre>'; print_r($vmon); echo '</pre>';
	//echo '<pre>'; print_r($vyear); echo '</pre>';
	//echo '<pre>'; print_r($cdate); echo '</pre>';
	//echo $cdate_check & $vid okkkkkk






//<-----------------------對獎-------------------------->	
	//$winning = false; //儲存是否中獎的變數


	for ($i=0; $i < count($vid) ; $i++) 
	{ 





		
		$dataarray_check = array();
		//把開獎號碼跟獎項抓出來
		$sqlcheck="SELECT `cid`,`cprice`,`cdate` FROM `checkinvoice` WHERE `cmon`='".$vmon[$i]."' AND `cyear`='".$vyear[$i]."'; ";
		//echo $sqlcheck;
		$result = $conn->query($sqlcheck);

		while ($row = $result->fetch_assoc()) 
		{
			$dataarray_check[] = $row;
		}
		//print_r($dataarray_check) ;


		//記得開獎號碼跟獎項
		$cid=array();
		$cprice=array();
		$cdate = array();

		foreach ($dataarray_check as $item) 
		{
			array_push($cid,$item['cid']);
			array_push($cprice,$item['cprice']);
			array_push($cdate,$item['cdate']);
		}
		/*echo '<pre>';
		
		print_r($cid);
		print_r($cprice);
		print_r($cdate);
		echo '</pre>';*/



		$winning = false;
		//對獎判斷
		for ($j=0; $j < count($cid); $j++) 
		{ 
			
			//先判斷所有大獎
			if (strtotime($cdate[$j]) < strtotime('today')) 
			{
			    //echo "已過期";

			    $sqlupdate = "UPDATE `invoice` SET `statue` = 'expired',`price` = '0' WHERE `invoice`.`vid` = '".$vid[$i]."';";
			    //echo $sqlupdate;
			    $resultupdate = $conn->query($sqlupdate);
			    break;
			}
			else
			{

				if ($cprice[$j] === 'special award') 
				{
	        		if ($vid[$i] === $cid[$j]) 
	        		{
	            		//echo $vid[$i]."特別獎！1,000萬。\n";
	            		$sqlupdate = "UPDATE `invoice` SET `statue`='winning' , `price`= '1000w' WHERE invoice.vid='".$vid[$i]."'; ";
	            		$resultupdate = $conn->query($sqlupdate);
	            		$winning = true;
	            		
	            		break;
	        		}
	    		} 
	    		elseif ($cprice[$j] === 'super award') 
	    		{
	        		if ($vid[$i] === $cid[$j]) 
	        		{
	            		//echo $vid[$i]."特獎！200萬元。\n";
	            		$sqlupdate = "UPDATE `invoice` SET `statue`='winning' , `price`= '200w' WHERE invoice.vid='".$vid[$i]."'; ";
	            		$resultupdate = $conn->query($sqlupdate);
	            		$winning = true;
	            		break;
	        		}
	    		} 
	    		elseif ($cprice[$j] === 'first award') 
	    		{
	        		if ($vid[$i] === $cid[$j]) 
	        		{
	            		//echo $vid[$i]."頭獎！20萬元。\n";
	            		$sqlupdate = "UPDATE `invoice` SET `statue`='winning' , `price`= '20w' WHERE invoice.vid='".$vid[$i]."'; ";
	            		$resultupdate = $conn->query($sqlupdate);
	            		$winning = true;
	            		break;
	        		}
	        		else
	        		{
	        			//比對中幾位數
	        			$price=array("0","0","0","200","1000","4000","1w","4w");
	        			for ($t=7; $t >= 3; $t--) 
	        			{ 	
	        				
	        				if (substr($vid[$i], -$t) === substr($cid[$j], -$t)) 
	        				{
	            				//echo $vid[$i]."中獎！\n";
	            				$sqlupdate = "UPDATE `invoice` SET `statue`='winning' , `price`='".$price[$t]."' WHERE invoice.vid = '".$vid[$i]."';";
	            				
	        					$resultupdate = $conn->query($sqlupdate);
	        					$winning = true;
	        					
	        					break;
	        				}
	        				

	        				
	        			}//比對中幾位數

			        


        			}

        		
    			}//'first award'

				if (!$winning)
				{
		        $sqlupdate = "UPDATE `invoice` SET `statue` = 'no award' ,`price`='0' WHERE invoice.vid = '".$vid[$i]."';";
		        $resultupdate = $conn->query($sqlupdate);
		        //echo $vid[$i]."未中獎\n";
		       
		        
		    	}//!$winning	

    		}


			



		}//for ($j=0; $j < count($cid_check); $j++) 




	


		
	}//for ($i=0; $i < count($vid_check) ; $i++) 
 

//<-----------------------------End---------------------------->


	//重新查一次使用者的發票再送出為新的發票資訊
	$sqlfinal="SELECT `vid`,`vmon`,`vyear`,`statue`,`price` FROM `invoice` WHERE `uid`='".$uid."' ORDER BY `vyear` DESC, `vmon` DESC;";
	$result = $conn->query($sqlfinal);

	$messageArr = array();
	$dataarray = array();

	if ($result->num_rows > 0) 
	{
	    // output data of each row
	    while ($row = $result->fetch_assoc()) 
	    {
	        $dataarray[] = $row;
	    }
	}

	//close connection
	$conn->close();
	
	//print result
	$messageArr["data"] = $dataarray;
	
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


	if(!empty($messageArr))
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
	
?>