# https://github.com/matrix-profile-foundation/mass-ts-examples

import numpy as np
import mass_ts as mts
from matplotlib import pyplot as plt


robot_dog = np.loadtxt('robot_dog.txt')
carpet_walk = np.loadtxt('carpet_query.txt')

plt.figure(figsize=(25,5))
plt.plot(np.arange(len(robot_dog)), robot_dog)
plt.ylabel('Accelerometer Reading')
plt.title('Robot Dog Sample')
plt.show()

plt.figure(figsize=(25,5))
plt.plot(np.arange(len(carpet_walk)), carpet_walk)
plt.ylabel('Accelerometer Reading')
plt.title('Carpet Walk Sample')
plt.show()

# Now we can search for the carpet walk snippet within the robot dog sample using MASS.
distances = mts.mass3(robot_dog, carpet_walk, 256)
min_idx = np.argmin(distances)
min_idx  # Output: 7479

# The minimum index found is the same as the author claims. We can now visualize this below.
plt.figure(figsize=(25,5))
plt.plot(np.arange(len(robot_dog)), robot_dog)
plt.plot(np.arange(min_idx, min_idx + 100), carpet_walk, c='r')
plt.ylabel('Accelerometer Reading')
plt.title('Robot Dog Sample')
plt.show()


# Plot the carpet walk query
fig, (ax1, ax2, ax3) = plt.subplots(3,1,sharex=True,figsize=(20,10))
ax1.plot(np.arange(len(carpet_walk)), carpet_walk)
ax1.set_ylabel('Carpet Walk Query', size=12)

# Plot use case best match from original authors
ax2.plot(np.arange(100), robot_dog[7478:7478+100])
ax2.set_ylabel('Original Best Match', size=12)

# Plot our best match
ax3.plot(np.arange(100), robot_dog[min_idx:min_idx+100])
ax3.set_ylabel('Our Best Match', size=12)

plt.show()