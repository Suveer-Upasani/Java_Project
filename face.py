from flask import Flask, render_template, Response
import cv2
import mediapipe as mp
import numpy as np

app = Flask(__name__)

# MediaPipe setup
mp_face_mesh = mp.solutions.face_mesh
mp_drawing = mp.solutions.drawing_utils

# Initialize camera
cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

face_mesh = mp_face_mesh.FaceMesh(
    static_image_mode=False,
    max_num_faces=5,
    refine_landmarks=True,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

# Track previous face centroid
prev_centroid = None
movement_threshold = 10  # sensitivity (pixels)

def generate_frames():
    global prev_centroid
    while True:
        success, image = cap.read()
        if not success:
            break

        image = cv2.flip(image, 1)
        rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        results = face_mesh.process(rgb_image)

        if results.multi_face_landmarks:
            if len(results.multi_face_landmarks) > 1:
                cv2.putText(image, "ALERT: Multiple faces detected!",
                            (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 3)

            for face_landmarks in results.multi_face_landmarks:
                h, w, _ = image.shape

                # Calculate centroid of all 468 landmarks
                points = np.array([(lm.x * w, lm.y * h) for lm in face_landmarks.landmark])
                centroid = np.mean(points, axis=0)

                # Movement detection
                movement_detected = False
                if prev_centroid is not None:
                    dist = np.linalg.norm(centroid - prev_centroid)
                    if dist > movement_threshold:
                        movement_detected = True

                prev_centroid = centroid
                color = (0, 255, 0) if not movement_detected else (0, 0, 255)

                # Draw full face mesh in green/red
                mp_drawing.draw_landmarks(
                    image=image,
                    landmark_list=face_landmarks,
                    connections=mp_face_mesh.FACEMESH_TESSELATION,
                    landmark_drawing_spec=None,
                    connection_drawing_spec=mp_drawing.DrawingSpec(
                        color=color, thickness=1, circle_radius=1
                    )
                )

        # Encode frame
        ret, buffer = cv2.imencode('.jpg', image)
        frame = buffer.tobytes()

        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')


@app.route('/')
def index():
    return render_template('test.html')

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')


if __name__ == '__main__':
    try:
        print("Face Mesh Visualization Starting...")
        print("Camera initialized")
        print("Server starting on http://localhost:5005")
        app.run(host="0.0.0.0", port=5005, debug=False)
    except KeyboardInterrupt:
        print("\nApplication stopped")
    finally:
        cap.release()
        cv2.destroyAllWindows()
