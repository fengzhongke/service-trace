G6.registerNode('rect', {
  getPath: function getPath(item) {
    var width = 200; // 一半宽
    var height = 30; // 一半高
    return G6.Util.getRectPath(-width / 2, -height / 2, width, height, 5);
  }
});

G6.registerEdge('smooth', {
  getPath: function getPath(item) {
    var fromx = item.source.model.x + 100;
    var fromy = item.source.model.y;
    var tox = item.target.model.x - 100;
    var toy = item.target.model.y;
    var hsize = tox - fromx;
    var vsize = toy - fromy;
    return [['M', fromx, fromy],
      ['C', fromx + hsize / 4, fromy, tox - hsize / 2, toy, tox, toy]];
  }
});

var getTooltip = function (tooltip) {
  var content = '<div class="g6-tooltip">';
  content += '<ul class="g6-tooltip-list">';
  for (var i in tooltip) {
    content += '<li class="g6-tooltip-list-item">';
    if(tooltip[i].length > 100){
      content += '<span>' + i + ':';
      for(var j=0; j<tooltip[i].length; j+=100){
        content += tooltip[i].substring(j, Math.min(j+100, tooltip[i].length)) + "<br>";
      }
      content += '</span>';
    }else{
      content += '<span>' + i + ':' + tooltip[i] + '</span>';;
    }
    content += '</li>';
  }
  content += '</ul>';
  content += '</div>';
  return content;
}

var getTree = function (id, height, width, direction) {
  var tree = new G6.Tree({
    id: id,
    height: height,
    width: width,
    fitView: 'tl',
    showButton: true,
    modes: {
      default: ['wheelZoom', 'panCanvas', 'panNode']
    },
    plugins: [new G6.Plugins['tool.tooltip']({
      getTooltip({item}) {
        var model = item.model;
        if (model.tooltip) {
          return getTooltip(model.tooltip);
        }
        return null;
      }
    })],
    layout: new G6.Layouts.CompactBoxTree({
      direction: direction, // 方向（LR/RL/H/TB/BT/V）
      getHGap: function getHGap() {
        return 20;
      },
      getVGap: function getVGap() {
        return 1;
      }
    })
  });
  tree.node({
    size: 10,
    shape: 'rect',
    color: function color(model) {
      if (model.color) {
        return model.color;
      } else {
        return '';
      }
    },
    label: function label(model) {
      var name = model.name;
      if(name.length > 20){
        name = name.substring(0, 17) + "...";
      }
      if(model.cnt){
        name += "[" + model.cnt + "]";
      }
      var fill = "black";
      if(model.tooltip.err > 0){
        fill = 'red';
      }
      return {
        text: name,
        textAlign: 'left',
        fill: fill,
        fontFamily: 'Courier'
      };
    },
    labelOffsetX: function labelOffsetX(model) {
      return -95;
    }
  });
  tree.edge({
    shape: 'smooth',
    style: {
      endArrow: true
    }
  });
  return tree;
}

var m = null;
var drawNode = function(docId, id){
  $.get('../node?id=' + id, function(msg){
    m = $.parseJSON(msg);
    var tree = getTree(docId, 1200, 1800, 'LR');
    tree.read({
      roots: [m]
    });
  });
}
var drawScene = function(docId, id){
  $.get('../scene?id=' + id, function(msg){
    m = $.parseJSON(msg);
    var tree = getTree(docId, 1200, 1800, 'LR');
    tree.read({
      roots: [m]
    });
  });
}