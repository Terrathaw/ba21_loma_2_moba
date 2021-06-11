# ba21_loma_2_moba
An android app that provides real-time dsp mutation options on music

## Testing
Use the test bench in src/test/java/ch.zhaw.ch/ generate transformed output for comparison.
Additional test files can be added in src/test/res/audio/in/.
The generated output files can be found in src/test/res/audio/out/.

## Output Files on App
Any audio file that is streamed on the app is written to /Android/data/ch.zhaw.ch/files with the same name as the original file.
The app releases the file only after the song has ended or another song has been selected.
The written file contains the exact data that has been streamed to the phones speakers.

## Algorithms
| Name  | Description | Status | Source|
| ------------- | ------------- | ------------- | ------------ |
| Basic Phase Shifter  | Simple phase vocoder limited to horizontal phase propagation  | Complete  | Based on DAFX Chapter 7.4.4 Block by block approach http://dafx.de/DAFX_Book_Page/index.html |
| Scaled Phase Locked Shifter  | Advanced phase vocoder with connected peak detection and phase locking | Complete  | Based on Improved Phase Vocoder Time-Scale Modification of Audio Scaled Phase locking https://ieeexplore.ieee.org/document/759041 |
| Dynamic Phase Locked Shifter  | Advanced phase vocoder with peak detection and dynamic vertical phase propagation | In Progress  | Based on Phase Vocoder Done Right pseudo code https://www.researchgate.net/publication/319503719_Phase_Vocoder_Done_Right |
| Rubberband | The rubberband audio library from breakfastquay integrated with default settings (-c 5) optimized for percussion | Integrated | Made available by breakfastquay  https://breakfastquay.com/rubberband/ |
