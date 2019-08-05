
var getName = function (node) {
  var name = node.tooltip.clazz;
  var idx = name.lastIndexOf('.');
  if(idx > 0){
    name = name.substring(idx+1);
  }
  return name + "[" +  node.tooltip.type + "]";
}
var getSequence = function(from, node){
  var sequence = null;
  if(node.hasOwnProperty('children')){
    sequence = [];
    for(var i in node.children){
      var child = node.children[i];
      var to = getName(child);
      var subSequence = getSequence(to, child);
      var invoke = child.name;
      if(invoke.length > 20){
          invoke += "\\n";
      }
      var suffix = "[rt:" + child.tooltip.cost + "]";
      if(child.tooltip.err > 0){
        suffix += "[err:" + child.tooltip.err + "]";
      }
      if(child.tooltip.doing > 0){
        suffix = "[doing:" + child.tooltip.doing + "]";
      }
      if(subSequence){
        sequence.push(from + "->" + to + ":" + invoke);
        sequence = sequence.concat(subSequence);
        sequence.push(to + "-->" + from + ":" + child.name + suffix);
      }else{
        sequence.push(from + "->" + to + ":" + invoke + suffix);
      }
    }
  }
  return sequence;
}
var m = null;
var drawSequence = function(docId, id){
  $.get('../node?id=' + id, function(msg){
    m = $.parseJSON(msg);
    var name = getName(m);
    var sequence = getSequence(name, m);
    var text = "Note left of " + name + ":" + "method:" + m.name + "\\n[rt:" + m.tooltip.cost + "]";
    if(m.tooltip.err > 0){
      text += "\\n[err]";
    }
    if(m.tooltip.doing > 0){
      text += "\\n[unfinished]";
    }
    for(var i in sequence){
      text += "\r\n" + sequence[i];
    }
    var diagram = Diagram.parse(text);
    diagram.drawSVG(docId, {theme: 'simple'});
  });
}