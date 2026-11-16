import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

export default function App() {
  return (
    <View style={styles.screen}>
      {/* top banner with the course name */}
      <View style={styles.banner}>
        <Text style={styles.bannerText}>COMP3074 - Lab 7</Text>
      </View>

      {/* main box with my info */}
      <View style={styles.infoBox}>
        <Text style={styles.label}>NAME</Text>
        <Text style={styles.name}>Arman Milani</Text>

        <View style={styles.line} />

        <Text style={styles.label}>STUDENT ID</Text>
        <Text style={styles.id}>101555606</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: '#14213D',
    justifyContent: 'flex-start', // everything starts at the top
    alignItems: 'center',
  },
  banner: {
    width: '100%',
    backgroundColor: '#FCA311',
    paddingTop: 50, // extra space so it clears the status bar
    paddingBottom: 15,
    alignItems: 'center',
  },
  bannerText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#14213D',
  },
  infoBox: {
    width: '80%',
    marginTop: 80,
    padding: 24,
    backgroundColor: '#E5E5E5',
    borderRadius: 8,
    borderLeftWidth: 8,
    borderLeftColor: '#FCA311',
  },
  label: {
    fontSize: 12,
    letterSpacing: 2,
    color: '#555555',
  },
  name: {
    fontSize: 30,
    fontWeight: 'bold',
    color: '#14213D',
    marginTop: 4,
  },
  line: {
    height: 1,
    backgroundColor: '#999999',
    marginVertical: 18,
  },
  id: {
    fontSize: 24,
    color: '#14213D',
    marginTop: 4,
  },
});
