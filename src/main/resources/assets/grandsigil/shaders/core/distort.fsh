#version 150

// The frame as it stood before this pass, copied aside so it can be read while the
// original is written over.
uniform sampler2D Sampler0;

// Where the column is on screen, in the same space as texCoord.
uniform vec2 Centre;
// How hard to pull, in screen widths. Zero leaves the frame untouched.
uniform float Strength;
// How hard the air shimmers, for ground being cooked from above. Independent of Strength:
// a star overhead wobbles the air for eight seconds without smearing anything.
uniform float Wobble;
// Seconds, more or less. Only the shimmer uses it.
uniform float Clock;
// Viewport aspect, so the falloff stays round on a wide monitor.
uniform float Ratio;
// How much colour to take out of the world, 0 to 1. The Gaze is the only thing that uses
// it: standing under something that is looking at you should not be a light show, and
// draining the frame is the one effect available here that reads as dread rather than as
// power. Applied last, so the smear and the shimmer are drained along with everything else.
uniform float Drain;
// What colour the light bleeding back over the picture is. Warm for a star, violet for an
// eye -- the same spill hard-coded for both would make one of them look like the other.
uniform vec3 Spill;
// How far the frame is pushed toward red. Crimson Tide, standing in it.
uniform float Blood;

in vec2 texCoord;
out vec4 fragColor;

const int TAPS = 8;

void main() {
    // Heat haze first, so the smear reads a frame that is already rippling rather than
    // rippling a frame that has already been smeared.
    vec2 base = texCoord;
    if (Wobble > 0.0001) {
        // Strongest low on the screen, because that is where the cooked ground is.
        float low = 1.0 - base.y;
        float ripple = sin(base.y * 46.0 - Clock * 3.1) * 0.6
                     + sin(base.y * 19.0 + base.x * 7.0 + Clock * 1.7) * 0.4;
        base.x += ripple * Wobble * low * low;
        base.y += sin(base.x * 31.0 + Clock * 2.3) * Wobble * 0.35 * low;
    }

    vec2 offset = base - Centre;
    // Measured with the aspect folded in, so "distance from the light" means the same
    // thing horizontally and vertically. The direction is taken without it, because the
    // smear has to run along the screen, not along a stretched copy of it.
    vec2 measured = vec2(offset.x * Ratio, offset.y);
    float radius = length(measured);
    vec2 direction = radius > 0.0001 ? normalize(offset) : vec2(0.0);

    // Hardest just off the middle and gone by the edges. The near-centre pixels barely
    // move, which is what keeps the light itself sharp while everything around it runs.
    float reach = Strength * radius * exp(-radius * 2.2);

    vec3 accumulated = vec3(0.0);
    float weight = 0.0;
    for (int i = 0; i < TAPS; i++) {
        float t = float(i) / float(TAPS - 1);
        float w = 1.0 - t * 0.55;
        accumulated += texture(Sampler0, base - direction * reach * t).rgb * w;
        weight += w;
    }
    vec3 colour = accumulated / weight;

    // The three channels land a little apart, which is the whole difference between a
    // blur and light coming apart.
    float split = reach * 0.35;
    colour.r = mix(colour.r, texture(Sampler0, base - direction * (reach + split)).r, 0.75);
    colour.b = mix(colour.b, texture(Sampler0, base - direction * max(reach - split, 0.0)).b, 0.75);

    // A little of the light itself spilling back over the picture.
    colour += Spill * Strength * exp(-radius * 5.0) * 1.6;

    if (Blood > 0.0001) {
        // Toward a red grade rather than a red wash: the luminance is kept and the channels
        // are reweighted, so the picture stays readable and simply stops being able to be
        // any other colour. A flat red overlay would just be a red overlay.
        float grey = dot(colour, vec3(0.2126, 0.7152, 0.0722));
        colour = mix(colour, vec3(grey) * vec3(1.55, 0.30, 0.36), Blood);
    }

    if (Drain > 0.0001) {
        // Rec. 709 luma, so the grey a colour collapses to is the brightness a person
        // actually perceives rather than the average of three numbers.
        float grey = dot(colour, vec3(0.2126, 0.7152, 0.0722));
        // Toward grey, and then a little further down: the world does not merely lose its
        // colour under this, it loses its nerve.
        colour = mix(colour, vec3(grey), Drain) * (1.0 - Drain * 0.28);
        // And a cold cast in the shadows, so the drained frame is not merely a photograph
        // of the same picture in black and white.
        colour += vec3(0.05, 0.01, 0.09) * Drain * (1.0 - grey);
    }

    fragColor = vec4(colour, 1.0);
}
