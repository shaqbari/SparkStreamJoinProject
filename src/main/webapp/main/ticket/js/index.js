(function () {

	var margin = {
		top: 10,
		bottom: 10,
		left: 10,
		right: 10
	};
	var width = 600 - margin.left - margin.right,
		height = 400 - margin.top - margin.bottom;

	var labelsWidth = 600 * 0.25;
	var labelWidth = labelsWidth * 0.2;
	var labelHeight = labelWidth * 0.6;

	var animateDuration = 300;
	var outerRadius = width-labelsWidth < height? (width-labelsWidth)/2 : height/2;
	var innerRadius = outerRadius * 0.4; // 40%

	var arc = d3.svg.arc()
		.innerRadius(innerRadius)
		.outerRadius(outerRadius);

	var pie = d3.layout
		.pie()
		.sort(null)
		.value(getValue)
		.startAngle(1.1*Math.PI)
		.endAngle(3.1*Math.PI);

	var color = d3.scale.category20();

	var chart = d3.select('.chart')
		.attr({
			width: width + margin.left + margin.right,
			height: height + margin.top + margin.bottom
		})
		.append('g')
		.attr({
			class: 'labels',
			transform: 'translate(' + (width-(labelsWidth + margin.right)) + ', ' + margin.top + ')'
		})
		.select(getParent)
		.append('g')
		.attr({
			class: 'pie',
			transform: 'translate(' + (outerRadius + margin.left) + ', ' + (height/2 + margin.top) + ')'
		})
		.select(getParent)
		.append('g')
		.attr({
			class: 'main-legend',
			transform: 'translate(' + (outerRadius + margin.left) + ', ' + (outerRadius + margin.top) + ')'
		})
		.select(getParent);

	chart.select('.main-legend')
		.append('circle')
		.attr({
			class: 'border',
			opacity: 0,
			stroke: 'rgba(255,255,255,1)',
			fill: 'rgba(0,0,0,0)',
			r: innerRadius*0.64
		})
		.select(getParent)
		.append('circle')
		.attr({
			class: 'circle',
			opacity: 0,
			fill: 'rgba(255,255,255,1)',
			r: innerRadius*0.6
		})
		.select(getParent)
		.append('line')
		.attr({
			opacity: 0,
			stroke: 'rgba(255,255,255,1)',
			x1: 0,
			y1: 0,
			x2: 0,
			y2: 0
		})
		.select(getParent)
		.append('text')
		.attr({
			class: 'legend-title',
			'font-size': '18px',
			y: -1 * innerRadius * 0.13,
			'font-family': 'Arial',
			'text-anchor': 'middle',
			'alignment-baseline': 'middle',
			fill: '#fff'
		})
		.select(getParent)
		.append('text')
		.attr({
			class: 'description',
			'font-size': '14px',
			y: innerRadius * 0.13,
			'font-family': 'Arial',
			'text-anchor': 'middle',
			'alignment-baseline': 'middle',
			fill: '#fff'
		});

	function getParent() {
		return this.parentNode;
	}
	
	function getValue(d) {
		return d.value;
	}

	function createArray(length) {
		var data = new Array(length);
		var name = 'Test ';
		for (var i= 0; i<length; i++) {
			data[i] = {
				value: 10 + Math.round(Math.random() * 10),
				name: name + i + Math.round(Math.random() * 10)
			};
		}
		return data;
	}

	function getSum(data) {
		var _sum = 0;
		data.forEach(function addValue(item) { 
			_sum += item.value; 
		});
		return _sum;
	}
	
	function arcTween(d) {
		var i;
		function toArc(t) {
			return arc(i(t));
		}
		function toEndAngle(t) {
			d.endAngle = i(t);
			return arc(d);
		}

		if ('undefined' === typeof this._current) {
			i = d3.interpolate(d.startAngle+0.1, d.endAngle);
			this._current = d;
			return toEndAngle;
		}
		i = d3.interpolate(this._current, d);
		this._current = i(0);
		return toArc;
	}

	function labelAnimation(d) {
		return 'translate (' + arc.centroid(d) + ')';
	}

	function fill(d, i) {
		return color(i);
	}

	function selectArcAnimation(_d) {
		var dist = outerRadius * 0.05;
		_d.midAngle = ((_d.endAngle - _d.startAngle) / 2) + _d.startAngle;
		var x = Math.sin(_d.midAngle) * dist;
		var y = -Math.cos(_d.midAngle) * dist;
		return 'translate(' + x + ',' + y + ')';
	}

	function updatePie(data) {
		var sum = getSum(data);
		data = pie(data);
		var arcs = chart.select('.pie')
			.selectAll('.arc')
			.data(data);

		var isDelay = arcs.selectAll('path').length;
		var _duration = isDelay? animateDuration : animateDuration/2;

		function getPercent(d) {
			return ((d.value/sum)*100).toFixed(1) + '%';
		}

		function getLegend(d) {
			return d.data.name + ' | ' + d.data.value;
		}

		function liveLarge(d) {
			var angle = 360 * (d.value/sum);
			var arcLength = (Math.PI * (outerRadius/2) * angle) / 180;
			return arcLength > Math.PI*outerRadius*0.01;
		}

		function onMouseIn(d, i) {
			var label = chart.selectAll('.legend')
					.filter(function(_d, _i) {
						return i===_i;
					});
			if (!label.attr('data-exit')) {
				label.transition()
					.duration(animateDuration/2)
					.attr({
						opacity: 1,
						transform: 'translate(10, '+ ((labelHeight + 5) * i) +')'
					});
			}

			chart.selectAll('.arc')
				.filter(function(_d, _i) {
					return i===_i;
				})
				.transition()
				.duration(animateDuration/2)
				.attr('transform', selectArcAnimation);

			chart.select('.main-legend')
				.select('.border')
				.transition()
				.duration(animateDuration/2)
				.attr({
					opacity: 1,
					stroke: fill(null, i)
				});

			chart.select('.main-legend')
				.select('.circle')
				.transition()
				.duration(animateDuration/2)
				.attr({
					opacity: 1,
					fill: fill(null, i)
				});

			chart.select('.main-legend')
				.select('line')
				.transition()
				.duration(animateDuration/2)
				.attr({
					opacity: 1,
					stroke: fill(null, i),
					x1: Math.sin(d.midAngle) * (innerRadius*0.7),
					y1: -Math.cos(d.midAngle) * (innerRadius*0.7),
					x2: Math.sin(d.midAngle) * innerRadius,
					y2: -Math.cos(d.midAngle) * innerRadius
				});

			chart.select('.main-legend')
				.select('.legend-title')
				.text(d.data.name)
				.select(getParent)
				.select('.description')
				.text(d.data.value)
		}
		function onMouseOut(d, i) {
			chart.select('.main-legend')
					.selectAll(['.circle', '.border'])
					.transition()
					.duration(animateDuration/2)
					.attr({
						opacity: 0
					});

			chart.select('.main-legend')
					.select('line')
					.transition()
					.duration(animateDuration/2)
					.attr({
						opacity: 0
					});

			chart.select('.main-legend')
					.select('.legend-title')
					.text('')
					.select(getParent)
					.select('.description')
					.text('');

			if ('undefined' === typeof i) {
				return;
			}
			var label = chart.selectAll('.legend')
				.filter(function(_d, _i) {
					return i===_i;
				});

			if (!label.attr('data-exit')) {
				label.transition()
					.duration(animateDuration/2)
					.attr({
						opacity: 1,
						transform: 'translate(0, '+ ((labelHeight + 5) * i) +')'
					});
			}

			chart.selectAll('.arc')
				.filter(function(_d, _i) {
					return i===_i;
				})
				.transition()
				.duration(animateDuration/2)
				.attr({
					transform: 'translate(0,0)'
				});
		}

		onMouseOut();
		// update Pie

		arcs.enter()
			.append('g')
			.attr('class', 'arc')
			.on('mouseenter', onMouseIn)
			.on('mouseout', onMouseOut)
			.append('path')
			.attr({
				fill: fill
			})
			.select(getParent)
			.filter(liveLarge)
			.append('text')
			.attr({
				'font-family': 'Arial',
				'font-size': '14px',
				fill: '#fff',
				'text-anchor': 'middle'
			});
		
		arcs.select('path')
			.transition()
			.delay( function delayFn(d,i) {
				return isDelay? 0 : _duration*i;
			})
			.duration(_duration)
			.attrTween('d', arcTween);

		arcs.select('text')
			.text(getPercent)
			.transition()
			.duration(animateDuration)
			.attr({
				class: 'label-content',
				transform: labelAnimation
			});

		arcs.exit()
    	.on('mouseenter', null)
			.on('mouseout', null)
      .remove();

		// Update Legends

		var legends = chart
				.select('.labels')
				.selectAll('.legend')
				.data(data);

		legends.enter()
			.append('g')
			.on('mouseenter', onMouseIn)
			.on('mouseout', onMouseOut)
			.attr('class', 'legend')
			.append('rect')
			.attr({
				width: labelWidth,
				height: labelHeight,
				fill: fill
			})
			.select(getParent)
			.append('text')
			.attr({
				'font-size': '14px',
				'font-family': 'Arial',
				fill: '#666',
				x: labelWidth + 5,
				y: labelHeight/2 + 5
			})
			.select(getParent)
			.attr({
				opacity: 0,
				transform: function(d, i) {
					return 'translate(15, ' + ((labelHeight + 5) * i) + ')';
				}
			})
			.transition()
			.duration(animateDuration)
			.attr({
				opacity: 1,
				transform: function(d, i) {
					return 'translate(0, ' + ((labelHeight + 5) * i) + ')';
				}
			});

		legends.exit()
      .on('mouseenter', null)
			.on('mouseout', null)
			.attr('data-exit', true)
			.transition()
			.duration(animateDuration/2)
			.attr({
				opacity: 0,
				transform: function(d, i) {
					return 'translate(-15, ' + ((labelHeight + 5) * i) + ')';
				}
			})
			.remove();

		legends.select('text').text(getLegend);
	}
	
	function generatePie() {
		var data = createArray(Math.ceil(Math.random() * 10));
		//var data = [{name: 'test 1', value: 100}];
		updatePie(data);
		setTimeout(generatePie, 4000);
	}

	generatePie();
} ());