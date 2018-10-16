function drawCells(sizeX, sizeY, array) {
    console.log('drawing cells');
    var canvas = document.getElementById("canvas");
    var ctx = canvas.getContext("2d");
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.clearRect(0, 0, 600, 600);
        var sizeOfCell = Math.floor(600 / sizeX);

        for (var i = 0; i < sizeX; i++) {
            for (var j = 0; j < sizeY; j++) {
                ctx.fillStyle = 'rgb' + '(' + array[i][j].rgb[0] + ',' + array[i][j].rgb[1] + ',' + array[i][j].rgb[2] + ')';
                ctx.fillRect(i * sizeOfCell, j * sizeOfCell, sizeOfCell, sizeOfCell)
            }
        }
    }


