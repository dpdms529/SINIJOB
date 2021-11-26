<?php
header("Content-Type: text/html; charset=UTF-8");
?>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
	new daum.Postcode({
		oncomplete: function(data){
				if(data.userSelectedType=="R"){
					window.hanium.getAddress(data.sigunguCode,data.roadnameCode, data.roadAddress, data.buildingName);
				}
				else{
					if(data.roadAddress!=""){
						window.hanium.getAddress(data.sigunguCode,data.roadnameCode, data.roadAddress, data.buildingName);
					}else{
						window.hanium.getAddress(data.sigunguCode,data.roadnameCode, data.autoRoadAddress, data.buildingName);
					}
				}
		}
}).open();
</script>

