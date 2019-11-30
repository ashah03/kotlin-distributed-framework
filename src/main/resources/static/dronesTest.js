function setup() {
    createCanvas(500, 500);
    frameRate(2)
}

function draw() {
    clear()
    httpGet("http://localhost:8080/drones", function (response) {
        //print(response)
        var obj = JSON.parse(response)
        print(obj)
        //var drone
        for (var drone in obj) {
            print(obj[drone])
            circle(obj[drone].X * 5, obj[drone].Y * 5, 40)
        }
    })
}