console.log("hi");

const toggleSidebar = ()=>{
    if($(".sidebar").is(":visible")){
    	
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
        
    }
    else{
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

const search=()=>{
    let query = $("#search-content").val();
    // console.log(query);
    if(query == ''){
        $(".search-result").hide();
        $(".nofound").hide();
    }
    else{
      
       let url = `http://localhost:8080/search/${query}`;
       
       fetch(url).then((response)=>{
            return response.json();
       }).then((data)=> {
           console.log(data);
            let text = `<div class="list-group">`;
            data.forEach(contact => {
                text+= `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-item-action">${contact.name}</a>`;
            });
            
            text+= `</div>`;
            if(data.length > 0){
                 $(".nofound").hide();
                $(".search-result").html(text);
                $(".search-result").show();
            }else{
            	$(".search-result").hide();
        		$(".nofound").show();
            }
            
               
       });
    }
};



$( function() {
    $( "#mail" ).autocomplete({
      source: "autocomplete",
      minLength: 3
    });
  } );

