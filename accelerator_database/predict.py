import pandas as pd
from prophet import Prophet

# Load the data from the CSV file
data = pd.read_csv('accelerator_table.csv')

# Convert the timestamp to datetime format
data['timestamp'] = pd.to_datetime(data['timestamp'], unit='ms')

# Keep only unique timestamps
data = data.drop_duplicates(subset=['timestamp'])

# Rename the columns to match Prophet's expected format
data = data.rename(columns={'timestamp': 'ds', 'x': 'y'})

# Sort the DataFrame by timestamp
data = data.sort_values(by='ds')

# Create a Prophet model
model = Prophet()

# Fit the model to the data
model.fit(data)

# Create a future dataframe for the next 10 seconds
future = model.make_future_dataframe(periods=10, freq='S')

# Make predictions
forecast = model.predict(future)

# Plot the actual and predicted values
import matplotlib.pyplot as plt

plt.figure(figsize=(12, 6))
plt.plot(data['ds'], data['y'], label='Actual X')
plt.plot(forecast['ds'], forecast['yhat'], label='Predicted X')
plt.xlabel('Time')
plt.ylabel('Value')
plt.title('Actual vs Predicted Values for X')
plt.legend()
plt.show()
