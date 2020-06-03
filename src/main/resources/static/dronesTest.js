var canvasSizeX = 700
var canvasSizeY = 700

var scaleX = 0
var scaleY = 0

function setup() {
    createCanvas(canvasSizeX, canvasSizeY)
    frameRate(10)
    httpGet("http://localhost:8080/size", function (response) {
        var obj = JSON.parse(response)
        var x = obj["x"]
        var y = obj["y"]
        scaleX = canvasSizeX / (x + 1)
        scaleY = canvasSizeY / (y + 1)
    })

}

var k = 0;

function draw() {
    // if(k % 2 == 0) {
    httpGet("http://localhost:8080/data", function (response) {
        var weights = JSON.parse(response).weights.map
        // print(weights)
        // print(Object.keys(obj).length)
        for (var i = 0; i < Object.keys(weights).length; i += 2) {
            // print(weights[i])
            fill((1 - weights[i + 1]) * 180 + 75, (1 - weights[i + 1]) * 180 + 75, (1 - weights[i + 1]) * 180 + 75)
            // print(i)
            square(weights[i].X * scaleX, weights[i].Y * scaleY, scaleX)
            // print("Squares at:")
            // console.log(concat(weights[i].X.toString(), weights[i].Y))
        }
        // } else {

        //print(response)
        var drones = JSON.parse(response).drones.map
        // print(drones)
        //var drone
        for (var drone in drones) {
            // print(obj[drone])
            // var scale = 50
            var radius = scaleX / 1.5
            // var padding = radius + 5
            fill(255, 255, 255)
            circle(drones[drone].X * scaleX + scaleX / 2, drones[drone].Y * scaleY + scaleY / 2, radius)
            print(concat(concat("Displaying circle at", drones[drone].X), drones[drone].Y))
        }
    })
}

// function setup() {
//     createCanvas(2000, 2000);
//     frameRate(2)
// }
//
// function draw() {
//     clear()
//     httpGet("http://localhost:8080/drones", function (response) {
//         //print(response)
//         var obj = JSON.parse(response)["map"]
//         print(obj)
//         //var drone
//         for (var drone in obj) {
//             print(obj[drone])
//             var scale = 50
//             var radius = 40
//             var padding = radius / 2
//             circle(obj[drone].X * scale + padding, obj[drone].Y * scale + padding, radius)
//         }
//     })
// }