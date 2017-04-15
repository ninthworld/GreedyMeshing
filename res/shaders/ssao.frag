uniform sampler2D texture0;
uniform sampler2D texture1;

uniform vec2 camerarange;
uniform vec2 screensize;

float readDepth( in vec2 coord ) {
    return (2.0 * camerarange.x) / (camerarange.y + camerarange.x - texture2D( texture0, coord ).x * (camerarange.y - camerarange.x));  
}


void main(void)
{   
	vec2 texCoord = gl_TexCoord[0].st;
	//vec2 texCoord = texture2D(texture0, gl_TexCoord[0].st).xy;
	//vec3 texColor = texture2D(texture1, gl_TexCoord[0].st).rgb;
	
    float depth = readDepth( texCoord );
    float d;

    float pw = 1.0 / screensize.x;
    float ph = 1.0 / screensize.y;

    float aoCap = 0.45;

    float ao = 0.0;

    float aoMultiplier=1000.0;

    float depthTolerance = 0.00001;

    d=readDepth( vec2(texCoord.x+pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x+pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    pw*=2.0;
    ph*=2.0;
    aoMultiplier/=2.0;

    d=readDepth( vec2(texCoord.x+pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x+pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    pw*=2.0;
    ph*=2.0;
    aoMultiplier/=2.0;

    d=readDepth( vec2(texCoord.x+pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x+pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    pw*=2.0;
    ph*=2.0;
    aoMultiplier/=2.0;

    d=readDepth( vec2(texCoord.x+pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y+ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x+pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    d=readDepth( vec2(texCoord.x-pw,texCoord.y-ph));
    ao+=min(aoCap,max(0.0,depth-d-depthTolerance) * aoMultiplier);

    ao/=16.0;

    gl_FragColor = vec4(1.2-ao) * texture2D(texture1, texCoord);
}