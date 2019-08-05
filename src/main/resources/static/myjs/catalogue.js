
// function getTree(msg){
//   var nodes = [];
//   for(var key in msg){
//     if($.isPlainObject(msg[key])){
//       nodes.push({"text":""+key, "nodes":getTree(msg[key])});
//     }else{
//       nodes.push({"text":""+key, "nodes":getLeaf(msg[key])});
//     }
//   }
//   return nodes;
// }

function getTree(foldTreeElements){
  var nodes1 = [];
  for(var i in foldTreeElements){
    var foldTreeElement = foldTreeElements[i];
    var nodes2 = [];
    for(var j in foldTreeElement['clazzs']){
      var clazzElement = foldTreeElement['clazzs'][j];
      var nodes3 = [];
      for(var k in clazzElement['methods']){
        var nodes4 = [];
        var methodElement = clazzElement['methods'][k];
        for(var l in methodElement['nodes']){
          var nodeElement = methodElement['nodes'][l];
          nodes4.push({
              'text':'request ID['+nodeElement['id'] +
                  '] time[' + nodeElement['time'] +
                  '] thread[' + nodeElement['tid'] +
                  '] totalRt[' + nodeElement['totalRt'] +
                  '] invokeCnt[' + nodeElement['invokeCnt'] + "]",
            'href':"node?id=" + nodeElement['id']
          })
        }
        nodes3.push({'text':'method[' + methodElement['name'] + '] requestCnt:[' + methodElement['cnt'] + "]", 'nodes':nodes4, 'href':"scene?id=" + methodElement['mid']})
      }
      nodes2.push({'text':'class:' + clazzElement['name'], 'nodes':nodes3})
    }
    nodes1.push({'text':'entrance type:' + foldTreeElement['type'], 'nodes':nodes2})
  }
  return nodes1;
}

var m = null;
function getLeaf(arr){
  var leaf = [];
  for(var i in arr){
    leaf.push({"text":""+arr[i], "href":"node?id=" + arr[i]});
  }
  return leaf;
}
$.get('../list', function(msg){
  m = $.parseJSON(msg);
  $('#tree').treeview({
    data: getTree($.parseJSON(msg)),
    enableLinks: true});
});

$('#recordChange').click(function(){
  var record = $('#recordVal').html()=="false" ? true : false;
  $.get('../change', {record:record}, function(msg){
    var val = $.parseJSON(msg);
    if(val.hasOwnProperty("newVal")){
      $('#recordVal').html("" + val["newVal"]);
    }
  });
});
$.get('../record', function(msg){
  var val = $.parseJSON(msg);
  if(val.hasOwnProperty("oldVal")){
    $('#recordVal').html("" + val["oldVal"]);
  }
});

$('#recordClear').click(function(){
  $.get('../clear', function(msg){
    alert("clear ok!");
    location.reload();
  });
});


